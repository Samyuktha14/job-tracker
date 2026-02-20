package com.example.jobtracker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobtracker.model.User;
import com.example.jobtracker.repository.UserRepository;
import com.example.jobtracker.service.TelegramLinkService;

@RestController
@RequestMapping("/api/telegram")
public class TelegramLinkController {

    private final TelegramLinkService telegramLinkService;
    private final UserRepository userRepository;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public TelegramLinkController(
            TelegramLinkService telegramLinkService,
            UserRepository userRepository
    ) {
        this.telegramLinkService = telegramLinkService;
        this.userRepository = userRepository;
    }

    @PostMapping("/link-token")
    public Map<String, String> generateLinkToken(Authentication auth) {

       if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new AccessDeniedException("Unauthorized");
        }

        User user = (User) auth.getPrincipal();

        String token = telegramLinkService.generateLinkToken(user.getUid());

        return Map.of(
                "token", token,
                "telegramLink",
                "https://t.me/" + botUsername + "?start=" + token
        );
    }

    @DeleteMapping("/unlink")
    public Map<String, String> unlinkTelegram(Authentication auth) {

        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new RuntimeException("Unauthorized");
        }

        User user = (User) auth.getPrincipal();
        user.setTelegramChatId(null);
        userRepository.save(user);


        return Map.of("message", "Telegram unlinked successfully");
    }
}