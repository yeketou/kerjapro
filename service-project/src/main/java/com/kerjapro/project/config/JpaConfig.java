package com.kerjapro.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs == null) return Optional.of("system");
                String userId = attrs.getRequest().getHeader("X-User-Id");
                return Optional.ofNullable(userId).filter(s -> !s.isBlank());
            } catch (Exception e) {
                return Optional.of("system");
            }
        };
    }
}
