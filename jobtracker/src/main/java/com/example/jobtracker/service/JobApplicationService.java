package com.example.jobtracker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.repository.JobApplicationRepository;

@Service
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository jobRepository;

    // ===============================
    // ADMIN
    // ===============================

    public List<JobApplication> getAllJobs() {
        return jobRepository.findAll(
                Sort.by(Sort.Direction.DESC, "appliedDate")
        );
    }

    public Page<JobApplication> getAllJobsPaged(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    // ===============================
    // BASIC
    // ===============================

    public JobApplication getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found: " + id)
                );
    }

    // ===============================
    // CREATE
    // ===============================

    public JobApplication createJob(JobApplication job) {
        job.setReminderSent(false);
        job.setReminderSentAt(null);
        return jobRepository.save(job);
    }

    // ===============================
    // UPDATE
    // ===============================

    public JobApplication updateJob(Long id, JobApplication updated) {

        JobApplication existing = getJobById(id);

        existing.setCompanyName(updated.getCompanyName());
        existing.setRole(updated.getRole());
        existing.setAppliedDate(updated.getAppliedDate());
        existing.setApplicationStatus(updated.getApplicationStatus());
        existing.setNotes(updated.getNotes());
        existing.setCurrentStage(updated.getCurrentStage());
        existing.setCurrentRound(updated.getCurrentRound());
        existing.setTotalRounds(updated.getTotalRounds());
        existing.setSource(updated.getSource());
        existing.setResumeLink(updated.getResumeLink());
        existing.setReapplyDate(updated.getReapplyDate());

        existing.setNextActionType(updated.getNextActionType());
        existing.setNextActionAt(updated.getNextActionAt());
        existing.setReminderSent(false);
        existing.setReminderSentAt(null);

        if ("Rejected".equalsIgnoreCase(updated.getApplicationStatus())) {
            existing.setRejectionDate(
                    updated.getRejectionDate() != null
                            ? updated.getRejectionDate()
                            : LocalDate.now()
            );
        } else {
            existing.setRejectionDate(null);
        }

        return jobRepository.save(existing);
    }

    // ===============================
    // HARD DELETE
    // ===============================

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    // ===============================
    // QUERIES
    // ===============================

    public Page<JobApplication> getJobsByUser(String userId, Pageable p) {
        return jobRepository.findByUserId(userId, p);
    }

    public Page<JobApplication> getJobsByUserAndStatus(
            String userId,
            String status,
            Pageable p
    ) {
        return jobRepository
                .findByUserIdAndApplicationStatusIgnoreCase(
                        userId, status, p);
    }

    public Page<JobApplication> searchByUserAndCompany(
            String userId,
            String company,
            Pageable p
    ) {
        return jobRepository
                .findByUserIdAndCompanyNameContainingIgnoreCase(
                        userId, company, p);
    }

    public Page<JobApplication> getJobsByStatus(String status, Pageable p) {
        return jobRepository
                .findByApplicationStatusIgnoreCase(status, p);
    }

    public Page<JobApplication> searchByCompany(String company, Pageable p) {
        return jobRepository
                .findByCompanyNameContainingIgnoreCase(company, p);
    }
}