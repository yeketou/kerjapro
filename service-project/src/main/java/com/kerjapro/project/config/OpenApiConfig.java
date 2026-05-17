package com.kerjapro.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OperationCustomizer;

@Configuration
public class OpenApiConfig {

    private static final String USER_ID_SCHEME = "X-User-Id";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KerjaPro — Project Service")
                        .description("Projects and work packages management")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(USER_ID_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(USER_ID_SCHEME,
                                new SecurityScheme()
                                        .name(USER_ID_SCHEME)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Keycloak user subject. In production injected by gateway. For local dev enter any string e.g. test-mc-001")));
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
