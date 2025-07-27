package com.example.jobtracker.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class FirebaseTokenVerifier {

    public static FirebaseToken verify(String idToken) throws Exception {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e) {
            // Log a generic message (avoid printing token or stack trace in production)
            System.out.println("❌ Firebase token verification failed.");
            throw new Exception("Invalid or expired Firebase token");
        }
    }
}
