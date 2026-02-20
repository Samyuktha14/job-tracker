package com.example.jobtracker.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jobtracker.integration.TelegramService;
import com.example.jobtracker.model.TelegramLinkToken;
import com.example.jobtracker.model.User;
import com.example.jobtracker.repository.TelegramLinkTokenRepository;
import com.example.jobtracker.repository.UserRepository;

@Service
public class TelegramLinkService {

    private final TelegramLinkTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final TelegramService telegramService;

    public TelegramLinkService(
            TelegramLinkTokenRepository tokenRepository,
            UserRepository userRepository,
            TelegramService telegramService
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.telegramService = telegramService;
    }

    // ===============================
    //  Generate link token
    // ===============================
   public String generateLinkToken(String userId) {

    // delete old tokens for this user
    tokenRepository.deleteByUserId(userId);

    String token = UUID.randomUUID().toString();

    TelegramLinkToken linkToken = new TelegramLinkToken(
            token,
            userId,
            LocalDateTime.now().plusMinutes(10)
    );

    tokenRepository.save(linkToken);

    return token;
}


    // ===============================
    //  Link Telegram chat_id
    // ===============================
    @Transactional
    public void linkTelegramAccount(String token, Long chatId) {

        TelegramLinkToken linkToken = tokenRepository.findById(token)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid or expired token"));

        if (linkToken.isExpired()) {
            tokenRepository.delete(linkToken);
            throw new IllegalArgumentException("Token expired");
        }

        User user = userRepository.findById(linkToken.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

       
        // Link Telegram chat id to user
        user.setTelegramChatId(chatId);
        userRepository.save(user);

        // Token should never be reused
        tokenRepository.delete(linkToken);

        // Confirmation message to Telegram
        telegramService.sendMessage(
                chatId,
                "✅ Telegram linked successfully!\nYou will now receive job reminders here."
        );
    }
}
