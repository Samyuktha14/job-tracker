package com.example.jobtracker.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "telegram_link_token")
public class TelegramLinkToken {

    @Id
    @Column(length = 64)
    private String token;

    @Column(nullable = false)
    private String userId;   // Firebase UID

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // ===== Constructors =====

    public TelegramLinkToken() {
    }

    public TelegramLinkToken(String token, String userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    // ===== Helper =====

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    // ===== Getters & Setters =====

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
