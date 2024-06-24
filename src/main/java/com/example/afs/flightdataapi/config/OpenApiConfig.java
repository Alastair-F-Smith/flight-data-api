package com.example.afs.flightdataapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Flight data API", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiConfig.class);

    @Bean
    public OpenApiCustomizer addUnauthorisedResponse() {
        return customiseOperations(pathItem -> pathItem.readOperations().stream(),
                                   operation -> operation.getResponses().addApiResponse("401", jsonResponse("Not authenticated")));
    }

    @Bean
    public OpenApiCustomizer addForbiddenResponse() {
        return customiseOperations(this::getMutatingOperations,
                                   operation -> operation.getResponses()
                                           .addApiResponse("403", jsonResponse("Not authorised (admin privileges required)")));
    }

    private OpenApiCustomizer customiseOperations(Function<PathItem, Stream<Operation>> operationSelector,
                                                  Consumer<Operation> customiseOperation) {
        return openApi -> openApi.getPaths()
                                 .values()
                                 .stream()
                                 .flatMap(operationSelector)
                                 .forEach(customiseOperation);
    }

    private Stream<Operation> getMutatingOperations(PathItem pathItem) {
        return Stream.of(pathItem.getPost(), pathItem.getPut(), pathItem.getDelete())
                .filter(Objects::nonNull);
    }

    private ApiResponse jsonResponse(String description) {
        return new ApiResponse().description(description)
                                .content(new Content()
                                                 .addMediaType("application/json", new io.swagger.v3.oas.models.media.MediaType()));
    }
}
