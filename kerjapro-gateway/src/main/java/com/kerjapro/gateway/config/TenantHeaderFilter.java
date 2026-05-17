package com.kerjapro.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Extracts user identity from the validated JWT and injects
 * downstream headers so services don't need to parse tokens.
 *
 * X-User-Id    → Keycloak subject (used for ownership checks & audit)
 * X-User-Roles → comma-separated roles (authorisation decisions)
 */
@Slf4j
@Component
public class TenantHeaderFilter extends AbstractGatewayFilterFactory<Object> {

    public static final String USER_ID_HEADER    = "X-User-Id";
    public static final String USER_ROLES_HEADER = "X-User-Roles";

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) ->
            ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth != null && auth.getPrincipal() instanceof Jwt)
                .map(auth -> (Jwt) auth.getPrincipal())
                .map(jwt -> {
                    var mutated = exchange.getRequest().mutate();
                    mutated.header(USER_ID_HEADER, jwt.getSubject());

                    var roles = jwt.getClaimAsStringList("roles");
                    if (roles != null && !roles.isEmpty()) {
                        mutated.header(USER_ROLES_HEADER, String.join(",", roles));
                    }

                    log.debug("Injected headers — userId={}", jwt.getSubject());
                    return exchange.mutate().request(mutated.build()).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}
