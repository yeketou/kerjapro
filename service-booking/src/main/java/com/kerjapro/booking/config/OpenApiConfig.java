package com.kerjapro.booking.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String USER_ID_SCHEME = "X-User-Id";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KerjaPro — Booking Service")
                        .description("Appointment booking with state machine: PENDING → CONFIRMED → COMPLETED")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(USER_ID_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(USER_ID_SCHEME,
                                new SecurityScheme()
                                        .name(USER_ID_SCHEME)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Keycloak user subject")));
    }

    @Bean
    public OperationCustomizer removeUserIdParam() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() != null) {
                operation.getParameters().removeIf(p ->
                        USER_ID_SCHEME.equals(p.getName()) && "header".equals(p.getIn()));
            }
            return operation;
        };
    }
}
