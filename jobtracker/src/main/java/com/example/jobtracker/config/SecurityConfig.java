package com.example.jobtracker.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.jobtracker.security.FirebaseTokenFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private FirebaseTokenFilter firebaseTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .authorizeHttpRequests(auth -> auth

                //  Preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                //  Public webhook
                .requestMatchers("/telegram/webhook").permitAll()

                // SUPER ADMIN only endpoints
                .requestMatchers("/api/super-admin/**")
                .hasRole("SUPER_ADMIN")

                //  ADMIN + SUPER_ADMIN
                .requestMatchers("/api/admin/**")
                .hasAnyRole("ADMIN", "SUPER_ADMIN")

                //  Any authenticated user
                .requestMatchers("/api/**")
                .authenticated()

                //  Everything else public
                .anyRequest().permitAll()
            )

            .addFilterBefore(
                firebaseTokenFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /*
     *  ROLE HIERARCHY (ENTERPRISE PATTERN)
     * SUPER_ADMIN > ADMIN > USER
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
            ROLE_SUPER_ADMIN > ROLE_ADMIN
            ROLE_ADMIN > ROLE_USER
        """);
        return hierarchy;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        config.setAllowedOrigins(
            Arrays.asList("http://localhost:5173")
        );

        config.setAllowedHeaders(Arrays.asList("*"));

        config.setAllowedMethods(
            Arrays.asList("GET","POST","PUT","DELETE","OPTIONS")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
