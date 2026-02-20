package com.example.jobtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class JobCreateDTO {

    @NotBlank(message = "Company name is required")
    @Size(max = 100)
    private String companyName;

    @NotBlank(message = "Role is required")
    @Size(max = 100)
    private String role;

    @NotNull(message = "Applied date is required")
    private LocalDate appliedDate;

    @NotBlank(message = "Application status is required")
    private String applicationStatus;

    private String currentStage;
    private Integer currentRound = 0;
    private Integer totalRounds = 0;

    // OLD FIELD (optional — remove later if unused)
    private LocalDate reminderDate;

    // =============================
    // NEW REMINDER SYSTEM
    // =============================

    private String nextActionType;

    private LocalDateTime nextActionAt;

    // =============================

    @Size(max = 1000)
    private String notes;

    private String source;

    @Pattern(
        regexp = "^(https?://.*)?$",
        message = "Resume link must be a valid URL"
    )
    private String resumeLink;

    // ===== Getters & Setters =====

    // (All getters and setters remain same — formatted cleanly)

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

    public LocalDate getReminderDate() { return reminderDate; }
    public void setReminderDate(LocalDate reminderDate) { this.reminderDate = reminderDate; }

    public String getNextActionType() { return nextActionType; }
    public void setNextActionType(String nextActionType) { this.nextActionType = nextActionType; }

    public LocalDateTime getNextActionAt() { return nextActionAt; }
    public void setNextActionAt(LocalDateTime nextActionAt) { this.nextActionAt = nextActionAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getResumeLink() { return resumeLink; }
    public void setResumeLink(String resumeLink) { this.resumeLink = resumeLink; }
}
