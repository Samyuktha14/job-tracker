package com.example.jobtracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
// 🧩 Add indexes to frequently queried columns
@Table(name = "job_application", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_company_name", columnList = "companyName"),
    @Index(name = "idx_applied_date", columnList = "appliedDate"),
    @Index(name = "idx_application_status", columnList = "applicationStatus")
})
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String companyName;
    private String role;
    private LocalDate appliedDate;
    private String applicationStatus;
    private LocalDate reminderDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rejectionDate;

    @Column(length = 1000)
    private String notes;

    private String currentStage;
    private Integer currentRound;
    private Integer totalRounds;
    private String source;

    
    private String resumeFileName;

    private LocalDate reapplyDate;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public LocalDate getReminderDate() { return reminderDate; }
    public void setReminderDate(LocalDate reminderDate) { this.reminderDate = reminderDate; }

    public LocalDate getRejectionDate() { return rejectionDate; }
    public void setRejectionDate(LocalDate rejectionDate) { this.rejectionDate = rejectionDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }

    public Integer getCurrentRound() { return currentRound; }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }

    public Integer getTotalRounds() { return totalRounds; }
    public void setTotalRounds(Integer totalRounds) { this.totalRounds = totalRounds; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public LocalDate getReapplyDate() { return reapplyDate; }
    public void setReapplyDate(LocalDate reapplyDate) { this.reapplyDate = reapplyDate; }
}
