package com.example.jobtracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.jobtracker.model.JobApplication;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    // ===============================
    // USER FILTERS
    // ===============================

    Page<JobApplication> findByUserId(
            String userId,
            Pageable pageable);

    Page<JobApplication> findByUserIdAndApplicationStatusIgnoreCase(
            String userId,
            String applicationStatus,
            Pageable pageable);

    Page<JobApplication> findByUserIdAndCompanyNameContainingIgnoreCase(
            String userId,
            String company,
            Pageable pageable);

    // ===============================
    // GLOBAL FILTERS
    // ===============================

    Page<JobApplication> findByApplicationStatusIgnoreCase(
            String applicationStatus,
            Pageable pageable);

    Page<JobApplication> findByCompanyNameContainingIgnoreCase(
            String company,
            Pageable pageable);

    // ===============================
    // ANALYTICS
    // ===============================

    long countByUserId(String userId);

    long countByApplicationStatusIgnoreCase(String applicationStatus);

    // ===============================
    // REMINDER QUERY
    // ===============================

    @Query("""
        SELECT j FROM JobApplication j
        JOIN User u ON j.userId = u.uid
        WHERE j.nextActionAt IS NOT NULL
        AND j.nextActionAt <= :now
        AND j.reminderSent = false
        AND j.applicationStatus NOT IN ('Rejected','Selected')
        AND u.telegramChatId IS NOT NULL
        ORDER BY j.nextActionAt ASC
    """)
    List<JobApplication> findDueReminders(
            @Param("now") LocalDateTime now
    );
}