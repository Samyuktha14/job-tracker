package com.example.jobtracker.controller;

import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/jobs")
public class JobApplicationController {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private JobApplicationService service;

    private String getAuthenticatedUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    // ✅ Get paged jobs
    @GetMapping("/paged")
    public Page<JobApplication> getJobsPaged(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size,
                                             @RequestParam(defaultValue = "appliedDate") String sortBy,
                                             @RequestParam(defaultValue = "desc") String sortDir) {
        String userId = getAuthenticatedUserId();
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return service.getJobsByUser(userId, pageable);
    }

    // ✅ Create job with resume
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createJob(@RequestParam("companyName") String companyName,
                                       @RequestParam("role") String role,
                                       @RequestParam("appliedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appliedDate,
                                       @RequestParam("applicationStatus") String status,
                                       @RequestParam(value = "currentStage", required = false) String currentStage,
                                       @RequestParam(value = "currentRound", required = false) Integer currentRound,
                                       @RequestParam(value = "totalRounds", required = false) Integer totalRounds,
                                       @RequestParam(value = "reminderDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reminderDate,
                                       @RequestParam(value = "notes", required = false) String notes,
                                       @RequestParam(value = "source", required = false) String source,
                                       @RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile) {

        try {
            String userId = getAuthenticatedUserId();
            JobApplication job = new JobApplication();
            job.setUserId(userId);
            job.setCompanyName(companyName);
            job.setRole(role);
            job.setAppliedDate(appliedDate);
            job.setApplicationStatus(status);
            job.setReminderDate(reminderDate);
            job.setNotes(notes);
            job.setSource(source);
            job.setCurrentStage(currentStage);
            job.setCurrentRound(currentRound != null ? currentRound : 0);
            job.setTotalRounds(totalRounds != null ? totalRounds : 0);

            if ("Rejected".equalsIgnoreCase(status)) {
                job.setRejectionDate(LocalDate.now());
            }

            if (resumeFile != null && !resumeFile.isEmpty()) {
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                String safeFilename = Paths.get(resumeFile.getOriginalFilename()).getFileName().toString();
                String finalName = System.currentTimeMillis() + "_" + safeFilename;
                Path filePath = Paths.get(UPLOAD_DIR, finalName);
                Files.write(filePath, resumeFile.getBytes(), StandardOpenOption.CREATE);
                job.setResumeFileName(finalName);
            }

            JobApplication saved = service.createJob(job);
            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving resume file.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating job.");
        }
    }

    // ✅ Update job
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody JobApplication updatedJob) {
        try {
            String userId = getAuthenticatedUserId();
            JobApplication existing = service.getJobById(id);

            if (!existing.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to update this job.");
            }

            updatedJob.setUserId(userId);
            JobApplication saved = service.updateJob(id, updatedJob);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
        }
    }

    // ✅ Delete job
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            String userId = getAuthenticatedUserId();
            JobApplication existing = service.getJobById(id);

            if (!existing.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to delete this job.");
            }

            service.deleteJob(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
        }
    }

    // ✅ Download resume
    @GetMapping("/download-resume/{filename}")
    public ResponseEntity<?> downloadResume(@PathVariable String filename) {
        String userId = getAuthenticatedUserId();
        JobApplication job = service.findByResumeFileName(filename);

        if (job == null) {
            return ResponseEntity.status(HttpStatus.GONE).body("Resume not found.");
        }

        if (!job.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }

        try {
            String safeFilename = Paths.get(filename).getFileName().toString();
            Path filePath = Paths.get(UPLOAD_DIR, safeFilename);

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file.");
        }
    }

    // ✅ Filter by status
    @GetMapping("/status")
    public Page<JobApplication> getJobsByStatus(@RequestParam String status,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size) {
        String userId = getAuthenticatedUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedDate").descending());
        return service.getJobsByUserAndStatus(userId, status, pageable);
    }

    // ✅ Search by company
    @GetMapping("/search")
    public Page<JobApplication> searchJobsByCompany(@RequestParam String company,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size) {
        String userId = getAuthenticatedUserId();
        Pageable pageable = PageRequest.of(page, size);
        return service.searchByUserAndCompany(userId, company, pageable);
    }
}
