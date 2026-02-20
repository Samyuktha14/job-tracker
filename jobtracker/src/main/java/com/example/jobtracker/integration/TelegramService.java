package com.example.jobtracker.integration;

import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {

    private static final Logger log =
            LoggerFactory.getLogger(TelegramService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RestTemplate restTemplate;

    public TelegramService() {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        this.restTemplate = new RestTemplate(factory);
    }

    public void sendMessage(Long chatId, String text) {

        String url =
            "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String, Object> body = Map.of(
            "chat_id", chatId,
            "text", text
        );

        try {

            restTemplate.postForObject(url, body, String.class);

            log.info("Telegram message sent to chatId {}", chatId);

        } catch (Exception e) {

            log.error("Failed to send Telegram message to chatId {}",
                    chatId, e);

            throw e; // Let scheduler decide what to do
        }
    }
}