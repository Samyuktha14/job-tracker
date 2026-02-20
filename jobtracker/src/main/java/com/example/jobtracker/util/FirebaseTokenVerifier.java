package com.example.jobtracker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public class FirebaseTokenVerifier {

    private static final Logger logger =
            LoggerFactory.getLogger(FirebaseTokenVerifier.class);

    public static FirebaseToken verify(String idToken) {

        try {
            return FirebaseAuth
                    .getInstance()
                    .verifyIdToken(idToken, true); // checks revoked tokens

        } catch (FirebaseAuthException e) {
            logger.warn("Invalid or expired Firebase token");
            throw new RuntimeException("Invalid or expired Firebase token");
        }
    }
}