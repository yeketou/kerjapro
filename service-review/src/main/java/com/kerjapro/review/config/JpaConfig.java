package com.kerjapro.review.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Optional;
@Configuration @EnableJpaAuditing
public class JpaConfig {
    @Bean public AuditorAware<String> auditorProvider() {
        return () -> { try { var a=(ServletRequestAttributes)RequestContextHolder.getRequestAttributes(); if(a==null)return Optional.of("system"); String u=a.getRequest().getHeader("X-User-Id"); return Optional.ofNullable(u).filter(s->!s.isBlank()); } catch(Exception e){return Optional.of("system");} };
    }
}
