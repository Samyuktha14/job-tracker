package com.example.jobtracker.config;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

   
    @PostConstruct
    public void initializeFirebase() {
    try (InputStream serviceAccount =
         getClass().getResourceAsStream(
             "/firebase-service-account.json")) {

        if (serviceAccount == null) {
            throw new RuntimeException(
                "Firebase service account file not found");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                    GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);

        System.out.println("Firebase Initialized");

    } catch (Exception e) {
        throw new RuntimeException(
            "Firebase init failed", e);
    }
}
}