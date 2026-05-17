package com.kerjapro.review.config;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
    private static final String S = "X-User-Id";
    @Bean public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("KerjaPro — Review Service").description("Ratings and reviews after completed bookings").version("1.0.0"))
            .addSecurityItem(new SecurityRequirement().addList(S))
            .components(new Components().addSecuritySchemes(S,new SecurityScheme().name(S).type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)));
    }
    @Bean public OperationCustomizer removeUserIdParam() {
        return (op,hm)->{if(op.getParameters()!=null)op.getParameters().removeIf(p->S.equals(p.getName())&&"header".equals(p.getIn()));return op;};
    }
}
