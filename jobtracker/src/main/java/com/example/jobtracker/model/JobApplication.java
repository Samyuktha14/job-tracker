package com.example.jobtracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "job_application",
    indexes = {

        // ===== User / Filtering Indexes =====
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_company_name", columnList = "companyName"),
        @Index(name = "idx_applied_date", columnList = "appliedDate"),
        @Index(name = "idx_application_status", columnList = "applicationStatus"),

        // ===== Reminder Performance Indexes =====
        @Index(name = "idx_next_action_at", columnList = "next_action_at"),

        // Composite index for scheduler 
        @Index(
            name = "idx_reminder_lookup",
            columnList = "reminder_sent, next_action_at"
        )
    }
)
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= CORE FIELDS =================

    @Column(nullable = false)
    private String userId;

    private String companyName;
    private String role;
    private LocalDate appliedDate;
    private String applicationStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rejectionDate;

    @Column(length = 1000)
    private String notes;

    private String currentStage;

    @Column(nullable = false)
    private int currentRound = 0;

    @Column(nullable = false)
    private int totalRounds = 0;

    private String source;

    @Column(length = 1000)
    private String resumeLink;

    private LocalDate reapplyDate;



    // ================= REMINDER SYSTEM =================

    @Column(name = "next_action_type")
    private String nextActionType;

    @Column(name = "next_action_at")
    private LocalDateTime nextActionAt;

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public LocalDate getRejectionDate() {
        return rejectionDate;
    }

    public void setRejectionDate(LocalDate rejectionDate) {
        this.rejectionDate = rejectionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResumeLink() {
        return resumeLink;
    }

    public void setResumeLink(String resumeLink) {
        this.resumeLink = resumeLink;
    }

    public LocalDate getReapplyDate() {
        return reapplyDate;
    }

    public void setReapplyDate(LocalDate reapplyDate) {
        this.reapplyDate = reapplyDate;
    }

    // ===== Reminder Getters / Setters =====

    public String getNextActionType() {
        return nextActionType;
    }

    public void setNextActionType(String nextActionType) {
        this.nextActionType = nextActionType;
    }

    public LocalDateTime getNextActionAt() {
        return nextActionAt;
    }

    public void setNextActionAt(LocalDateTime nextActionAt) {
        this.nextActionAt = nextActionAt;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public LocalDateTime getReminderSentAt() {
        return reminderSentAt;
    }

    public void setReminderSentAt(LocalDateTime reminderSentAt) {
        this.reminderSentAt = reminderSentAt;
    }


}
