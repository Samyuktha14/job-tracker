package com.example.jobtracker.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.jobtracker.model.User;
import com.example.jobtracker.service.UserService;
import com.example.jobtracker.util.FirebaseTokenVerifier;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(FirebaseTokenFilter.class);

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No token → continue filter chain
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = header.substring(7);

            FirebaseToken decoded =
                    FirebaseTokenVerifier.verify(token);

            String uid = decoded.getUid();
            String email = decoded.getEmail();
            String name = decoded.getName();
            boolean emailVerified = decoded.isEmailVerified();

            //  Block unverified users
            if (!emailVerified) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"EMAIL_NOT_VERIFIED\"}");
                return;
            }

            // Fetch user from DB (create if not exists)
            User user = userService.findOrCreate(uid, email, name);

            //  Block soft-deleted users
            if (user.isDeleted()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"ACCOUNT_DELETED\"}");
                return;
            }

            //  Block disabled users
            if (!user.isActive()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"ACCOUNT_DISABLED\"}");
                return;
            }

            // Build authorities
            List<GrantedAuthority> authorities = new ArrayList<>();

            if (user.getRole() != null) {
                authorities.add(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                );
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities
                    );

            // Attach request details
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // Clear old context and set new authentication
            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RuntimeException e) {

            logger.warn("Invalid or expired Firebase token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"INVALID_TOKEN\"}");
            return;

        } catch (Exception e) {

            logger.error("Unexpected authentication error", e);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"AUTHENTICATION_FAILED\"}");
            return;
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}