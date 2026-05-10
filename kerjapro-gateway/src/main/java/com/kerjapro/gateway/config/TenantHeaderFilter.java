package com.kerjapro.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Extracts tenant_slug and user roles from the validated JWT,
 * then injects them as headers for downstream services.
 *
 * Downstream services read:
 *   X-Tenant-Slug  → TenantResolutionFilter → TenantContext → search_path
 *   X-User-Id      → audit / ownership checks
 *   X-User-Roles   → authorization decisions
 */
@Slf4j
@Component
public class TenantHeaderFilter extends AbstractGatewayFilterFactory<Object> {

    public static final String TENANT_SLUG_HEADER = "X-Tenant-Slug";
    public static final String USER_ID_HEADER     = "X-User-Id";
    public static final String USER_ROLES_HEADER  = "X-User-Roles";

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) ->
            ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth != null && auth.getPrincipal() instanceof Jwt)
                .map(auth -> (Jwt) auth.getPrincipal())
                .map(jwt -> {
                    var mutatedRequest = exchange.getRequest().mutate();

                    // Inject tenant slug (null for subcontractors — no header sent)
                    String tenantSlug = jwt.getClaimAsString("tenant_slug");
                    if (tenantSlug != null && !tenantSlug.isBlank()) {
                        mutatedRequest.header(TENANT_SLUG_HEADER, tenantSlug.toLowerCase());
                    }

                    // Inject user ID (Keycloak subject)
                    mutatedRequest.header(USER_ID_HEADER, jwt.getSubject());

                    // Inject roles as comma-separated string
                    var roles = jwt.getClaimAsStringList("roles");
                    if (roles != null && !roles.isEmpty()) {
                        mutatedRequest.header(USER_ROLES_HEADER, String.join(",", roles));
                    }

                    log.debug("Injected headers — tenantSlug={}, userId={}, roles={}",
                            tenantSlug, jwt.getSubject(), roles);

                    return exchange.mutate().request(mutatedRequest.build()).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}
