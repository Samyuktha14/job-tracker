package com.example.jobtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jobtracker.model.TelegramLinkToken;

@Repository
public interface TelegramLinkTokenRepository
        extends JpaRepository<TelegramLinkToken, String> {

                 void deleteByUserId(String userId);
}
