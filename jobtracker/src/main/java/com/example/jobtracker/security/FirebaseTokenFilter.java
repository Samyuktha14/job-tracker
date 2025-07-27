package com.example.jobtracker.security;

import com.example.jobtracker.util.FirebaseTokenVerifier;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        logger.debug("Request URL: {}", request.getRequestURI());

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.replace("Bearer ", "");
            try {
                FirebaseToken decodedToken = FirebaseTokenVerifier.verify(token);
                String userId = decodedToken.getUid();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authToken);

                logger.debug("Firebase token verified successfully for UID.");

            } catch (Exception e) {
                logger.error("Firebase token verification failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Firebase Token");
                return;
            }
        } else {
            logger.warn("No Authorization header found or not a Bearer token.");
        }

        filterChain.doFilter(request, response);
    }
}
