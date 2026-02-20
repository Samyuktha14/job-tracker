package com.example.jobtracker.controller;



import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobtracker.service.TelegramLinkService;


@RestController
@RequestMapping("/telegram/webhook")
public class TelegramWebhookController {

    private static final Logger logger =
            LoggerFactory.getLogger(TelegramWebhookController.class);

    private final TelegramLinkService telegramLinkService;

    @Value("${telegram.webhook.secret}")
    private String webhookSecret;

    public TelegramWebhookController(TelegramLinkService telegramLinkService) {
        this.telegramLinkService = telegramLinkService;
    }

    @PostMapping
        public String onUpdate(
                @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false)
                String secret,
                @RequestBody Map<String, Object> update) {
        
            try {

                if (secret == null || !secret.equals(webhookSecret)) {
                    logger.warn("Invalid Telegram webhook secret"); 
                    return "ok";
                }

                if (!update.containsKey("message")) return "ok";

                Map<String, Object> message =
                        (Map<String, Object>) update.get("message");

                String text = (String) message.get("text");
                Map<String, Object> chat =
                        (Map<String, Object>) message.get("chat");

                Long chatId = ((Number) chat.get("id")).longValue();

                System.out.println("Text received: " + text);

                if (text != null && text.startsWith("/start")) {

                    String[] parts = text.split(" ");

                if (parts.length > 1) {
                String token = parts[1].trim();
                telegramLinkService.linkTelegramAccount(token, chatId);
            } else {
                telegramLinkService.sendLinkInstruction(chatId);
            }
                }

        return "ok";

    } catch (Exception e) {
        logger.error("Telegram webhook error", e);
        return "ok";
    }
}
}