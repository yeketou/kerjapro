package com.kerjapro.review.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
@Configuration @EnableWebSecurity
public class SecurityConfig {
    @Bean @Profile("local | local-no-kafka")
    public SecurityFilterChain localSecurity(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(a->a.anyRequest().permitAll()).build();
    }
    @Bean @Profile("prod")
    public SecurityFilterChain prodSecurity(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(a->a
            .requestMatchers("GET","/api/reviews/subcontractor/**").permitAll()
            .requestMatchers("/actuator/health","/swagger-ui/**","/api-docs/**").permitAll()
            .anyRequest().authenticated()).build();
    }
}
