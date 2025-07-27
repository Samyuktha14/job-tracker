package com.example.jobtracker.repository;

import com.example.jobtracker.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // ✅ Existing filters
    Page<JobApplication> findByApplicationStatusIgnoreCase(String status, Pageable pageable);
    Page<JobApplication> findByCompanyNameContainingIgnoreCase(String company, Pageable pageable);

    // ✅ User-specific filters
    Page<JobApplication> findByUserId(String userId, Pageable pageable);
    Page<JobApplication> findByUserIdAndApplicationStatusIgnoreCase(String userId, String status, Pageable pageable);
    Page<JobApplication> findByUserIdAndCompanyNameContainingIgnoreCase(String userId, String company, Pageable pageable);

    // ✅ Sorting by appliedDate using indexed column
    Page<JobApplication> findByUserIdOrderByAppliedDateDesc(String userId, Pageable pageable);
    Page<JobApplication> findByUserIdAndApplicationStatusIgnoreCaseOrderByAppliedDateDesc(String userId, String status, Pageable pageable);
    Page<JobApplication> findByUserIdAndCompanyNameContainingIgnoreCaseOrderByAppliedDateDesc(String userId, String company, Pageable pageable);

    // ✅ Resume file fetch
    JobApplication findByResumeFileName(String resumeFileName);
}
