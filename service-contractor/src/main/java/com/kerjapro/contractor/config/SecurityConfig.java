package com.kerjapro.contractor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Local profile — no JWT, all endpoints open for dev */
    @Bean
    @Profile("local | local-no-kafka")
    public SecurityFilterChain localSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    /** Production — gateway already validated JWT; trust injected headers */
    @Bean
    @Profile("prod")
    public SecurityFilterChain prodSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/contractors/search/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/contractors/profiles/**").permitAll()
                        .requestMatchers("/actuator/health", "/swagger-ui/**", "/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                // No JWT validation here — gateway handles it
                // Auth is enforced via presence of X-User-Id header
                .build();
    }
}
