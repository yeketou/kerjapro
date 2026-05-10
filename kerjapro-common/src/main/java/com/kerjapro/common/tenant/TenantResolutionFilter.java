package com.kerjapro.common.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Resolves the current tenant from the incoming request.
 *
 * Resolution order:
 * 1. X-Tenant-Slug header (set by gateway after JWT validation in prod)
 * 2. JWT claim "tenant_slug" (parsed upstream by gateway)
 * 3. Subdomain: {slug}.kerjapro.com
 *
 * Subcontractor requests carry NO tenant context (public marketplace access).
 * Main contractor requests MUST carry a tenant slug.
 */
@Slf4j
@Component
@Order(1)
public class TenantResolutionFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-Slug";
    private static final String MDC_TENANT_KEY = "tenantSlug";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String tenantSlug = resolveTenantSlug(request);

            if (tenantSlug != null && !tenantSlug.isBlank()) {
                TenantContext.setTenantSlug(tenantSlug.toLowerCase().trim());
                MDC.put(MDC_TENANT_KEY, tenantSlug);
                log.debug("Tenant resolved: {}", tenantSlug);
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
            MDC.remove(MDC_TENANT_KEY);
        }
    }

    private String resolveTenantSlug(HttpServletRequest request) {
        // 1. Header (injected by gateway)
        String header = request.getHeader(TENANT_HEADER);
        if (header != null && !header.isBlank()) {
            return header;
        }

        // 2. Subdomain: {slug}.kerjapro.com
        String host = request.getServerName();
        if (host != null && host.contains(".kerjapro.com")) {
            String subdomain = host.split("\\.")[0];
            if (!subdomain.equals("www") && !subdomain.equals("api")) {
                return subdomain;
            }
        }

        return null;
    }
}
