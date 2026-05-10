package com.kerjapro.contractor.config;

import com.kerjapro.common.tenant.TenantConnectionProvider;
import com.kerjapro.common.tenant.TenantIdentifierResolver;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class JpaConfig {

    private final TenantConnectionProvider  connectionProvider;
    private final TenantIdentifierResolver  identifierResolver;

    /**
     * Wires multi-tenancy into Hibernate via schema-based strategy.
     * search_path is set per-connection by TenantConnectionProvider.
     */
    @Bean
    public HibernatePropertiesCustomizer hibernateMultiTenancy() {
        return props -> {
            props.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
            props.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,  identifierResolver);
        };
    }

    /**
     * Supplies the current user ID for @CreatedBy / @LastModifiedBy audit fields.
     * Reads X-User-Id header injected by the gateway.
     */
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
