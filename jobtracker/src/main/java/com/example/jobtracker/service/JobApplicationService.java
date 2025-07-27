package com.example.jobtracker.service;

import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
//import java.util.Optional;

@Service
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository repository;

    // 🔍 Admin-only (optional, not used in user-side fetch)
    public List<JobApplication> getAllJobs() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "appliedDate"));
    }

    // 🔎 Get by ID (used for update, delete)
    public JobApplication getJobById(Long id) {
       return repository.findById(id)
    .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

    }

    // ➕ Create new job
    public JobApplication createJob(JobApplication job) {
        return repository.save(job);
    }

    // ✏️ Update job (already authorized in controller)
    public JobApplication updateJob(Long id, JobApplication updatedJob) {
        JobApplication existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        existing.setCompanyName(updatedJob.getCompanyName());
        existing.setRole(updatedJob.getRole());
        existing.setAppliedDate(updatedJob.getAppliedDate());
        existing.setApplicationStatus(updatedJob.getApplicationStatus());
        existing.setReminderDate(updatedJob.getReminderDate());
        existing.setNotes(updatedJob.getNotes());
        existing.setCurrentStage(updatedJob.getCurrentStage());
        existing.setCurrentRound(updatedJob.getCurrentRound());
        existing.setTotalRounds(updatedJob.getTotalRounds());
        existing.setSource(updatedJob.getSource());
        existing.setResumeFileName(updatedJob.getResumeFileName());

        // 🎯 Rejection logic
        if (!"Rejected".equalsIgnoreCase(updatedJob.getApplicationStatus())) {
            existing.setRejectionDate(null);
        } else {
            if (updatedJob.getRejectionDate() != null) {
                existing.setRejectionDate(updatedJob.getRejectionDate()); // ✅ Use custom date if available
            } else {
                existing.setRejectionDate(LocalDate.now()); // fallback
            }
        }
        existing.setReapplyDate(null); // reserved for future use

        return repository.save(existing);
    }

    // ❌ Delete job
    public void deleteJob(Long id) {
        repository.deleteById(id);
    }

    // 🔁 Pagination without filtering (for admin or testing)
    public Page<JobApplication> getJobs(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // 🔍 Filter by status (all users — not ideal for prod)
    public Page<JobApplication> getJobsByStatus(String status, Pageable pageable) {
        return repository.findByApplicationStatusIgnoreCase(status, pageable);
    }

    // 🔍 Search by company name (unfiltered)
    public Page<JobApplication> searchByCompany(String company, Pageable pageable) {
        return repository.findByCompanyNameContainingIgnoreCase(company, pageable);
    }

    // ✅ Multi-user support methods
    public Page<JobApplication> getJobsByUser(String userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable);
    }

    public Page<JobApplication> getJobsByUserAndStatus(String userId, String status, Pageable pageable) {
        return repository.findByUserIdAndApplicationStatusIgnoreCase(userId, status, pageable);
    }

    public Page<JobApplication> searchByUserAndCompany(String userId, String company, Pageable pageable) {
        return repository.findByUserIdAndCompanyNameContainingIgnoreCase(userId, company, pageable);
    }

    public JobApplication findByResumeFileName(String fileName) {
    return repository.findByResumeFileName(fileName);
}

}
