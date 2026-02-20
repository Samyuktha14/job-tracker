package com.example.jobtracker.dto;

import java.time.LocalDate;

public class UserDTO {

    private String uid;
    private String email;
    private String displayName;

    private boolean active;
    private boolean deleted;  

    private String role;
    private LocalDate createdAt;

    private boolean telegramLinked;
   


    // ===== getters & setters =====

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public boolean isTelegramLinked() { return telegramLinked; }
    public void setTelegramLinked(boolean telegramLinked) {
        this.telegramLinked = telegramLinked;
    }

}
