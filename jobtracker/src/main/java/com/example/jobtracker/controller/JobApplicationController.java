package com.example.jobtracker.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.jobtracker.dto.JobCreateDTO;
import com.example.jobtracker.dto.JobResponseDTO;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.User;
import com.example.jobtracker.service.JobApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
public class JobApplicationController {

    @Autowired
    private JobApplicationService service;

    // ===============================
    // AUTH HELPER
    // ===============================
    private User currentUser(Authentication auth) {
        Object principal = auth.getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        throw new RuntimeException("Unauthenticated user");
    }

    // ===============================
    // GET PAGED JOBS
    // ===============================
    @GetMapping("/paged")
    public Page<JobResponseDTO> getJobsPaged(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        User user = currentUser(auth);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("appliedDate").descending()
        );

        return service
                .getJobsByUser(user.getUid(), pageable)
                .map(this::toDTO);
    }

    // ===============================
    // CREATE JOB
    // ===============================
    @PostMapping
    public ResponseEntity<JobResponseDTO> createJob(
            Authentication auth,
            @Valid @RequestBody JobCreateDTO dto) {

        User user = currentUser(auth);

        JobApplication job = new JobApplication();
        job.setUserId(user.getUid());
        job.setCompanyName(dto.getCompanyName());
        job.setRole(dto.getRole());
        job.setAppliedDate(dto.getAppliedDate());
        job.setApplicationStatus(dto.getApplicationStatus());
        job.setNotes(dto.getNotes());
        job.setSource(dto.getSource());
        job.setCurrentStage(dto.getCurrentStage());
        job.setCurrentRound(dto.getCurrentRound());
        job.setTotalRounds(dto.getTotalRounds());
        job.setResumeLink(dto.getResumeLink());

        // Reminder system
        job.setNextActionType(dto.getNextActionType());
        job.setNextActionAt(dto.getNextActionAt());
        job.setReminderSent(false);

        // Rejection logic
        if ("Rejected".equalsIgnoreCase(dto.getApplicationStatus())) {
            job.setRejectionDate(LocalDate.now());
        }

        return ResponseEntity.ok(toDTO(service.createJob(job)));
    }

    // ===============================
    // UPDATE JOB
    // ===============================
    @PutMapping("/{id}")
    public ResponseEntity<JobResponseDTO> updateJob(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody JobCreateDTO dto) {

        User user = currentUser(auth);
        JobApplication existing = service.getJobById(id);

        if (!existing.getUserId().equals(user.getUid())) {
            return ResponseEntity.status(403).build();
        }

        existing.setCompanyName(dto.getCompanyName());
        existing.setRole(dto.getRole());
        existing.setAppliedDate(dto.getAppliedDate());
        existing.setApplicationStatus(dto.getApplicationStatus());
        existing.setNotes(dto.getNotes());
        existing.setCurrentStage(dto.getCurrentStage());
        existing.setCurrentRound(dto.getCurrentRound());
        existing.setTotalRounds(dto.getTotalRounds());
        existing.setSource(dto.getSource());
        existing.setResumeLink(dto.getResumeLink());

        // Reminder update
        existing.setNextActionType(dto.getNextActionType());
        existing.setNextActionAt(dto.getNextActionAt());
        existing.setReminderSent(false);

        // Rejection logic
        if ("Rejected".equalsIgnoreCase(dto.getApplicationStatus())) {
            existing.setRejectionDate(LocalDate.now());
        } else {
            existing.setRejectionDate(null);
        }

        return ResponseEntity.ok(
                toDTO(service.updateJob(id, existing))
        );
    }

    // ===============================
    // DELETE JOB (Hard Delete)
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            Authentication auth,
            @PathVariable Long id) {

        User user = currentUser(auth);
        JobApplication job = service.getJobById(id);

        if (!job.getUserId().equals(user.getUid())) {
            return ResponseEntity.status(403).build();
        }

        service.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // FILTER BY STATUS
    // ===============================
    @GetMapping("/status")
    public Page<JobResponseDTO> getJobsByStatus(
            Authentication auth,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        User user = currentUser(auth);
        Pageable pageable = PageRequest.of(page, size);

        return service
                .getJobsByUserAndStatus(user.getUid(), status, pageable)
                .map(this::toDTO);
    }

    // ===============================
    // SEARCH BY COMPANY
    // ===============================
    @GetMapping("/search")
    public Page<JobResponseDTO> searchJobsByCompany(
            Authentication auth,
            @RequestParam String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        User user = currentUser(auth);
        Pageable pageable = PageRequest.of(page, size);

        return service
                .searchByUserAndCompany(user.getUid(), company, pageable)
                .map(this::toDTO);
    }

    // ===============================
    // ENTITY → DTO
    // ===============================
    private JobResponseDTO toDTO(JobApplication job) {

        JobResponseDTO dto = new JobResponseDTO();

        dto.setId(job.getId());
        dto.setCompanyName(job.getCompanyName());
        dto.setRole(job.getRole());
        dto.setAppliedDate(job.getAppliedDate());
        dto.setApplicationStatus(job.getApplicationStatus());
        dto.setCurrentStage(job.getCurrentStage());
        dto.setCurrentRound(job.getCurrentRound());
        dto.setTotalRounds(job.getTotalRounds());
        dto.setNextActionType(job.getNextActionType());
        dto.setNextActionAt(job.getNextActionAt());
        dto.setReminderSent(job.isReminderSent());
        dto.setRejectionDate(job.getRejectionDate());
        dto.setNotes(job.getNotes());
        dto.setSource(job.getSource());
        dto.setResumeLink(job.getResumeLink());

        return dto;
    }
}
