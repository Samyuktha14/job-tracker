package com.example.jobtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobResponseDTO {

    private Long id;
    private String companyName;
    private String role;
    private LocalDate appliedDate;
    private String applicationStatus;
    private String currentStage;
    private Integer currentRound;
    private Integer totalRounds;
    private LocalDate rejectionDate;

    // =============================
    // REMINDER FIELDS
    // =============================

    private String nextActionType;
    private LocalDateTime nextActionAt;
    private boolean reminderSent;

    // =============================

    private String notes;
    private String source;
    private String resumeLink;
   

    // ===== Getters & Setters =====

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

    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }

    public Integer getCurrentRound() { return currentRound; }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }

    public Integer getTotalRounds() { return totalRounds; }
    public void setTotalRounds(Integer totalRounds) { this.totalRounds = totalRounds; }

    public LocalDate getRejectionDate() { return rejectionDate; }
    public void setRejectionDate(LocalDate rejectionDate) { this.rejectionDate = rejectionDate; }

    public String getNextActionType() { return nextActionType; }
    public void setNextActionType(String nextActionType) { this.nextActionType = nextActionType; }

    public LocalDateTime getNextActionAt() { return nextActionAt; }
    public void setNextActionAt(LocalDateTime nextActionAt) { this.nextActionAt = nextActionAt; }

    public boolean isReminderSent() { return reminderSent; }
    public void setReminderSent(boolean reminderSent) { this.reminderSent = reminderSent; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getResumeLink() { return resumeLink; }
    public void setResumeLink(String resumeLink) { this.resumeLink = resumeLink; }

   
}
