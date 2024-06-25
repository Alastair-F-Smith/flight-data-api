package com.example.afs.flightdataapi.config;

import com.example.afs.flightdataapi.controllers.documentation.ExampleData;
import com.example.afs.flightdataapi.controllers.documentation.ExampleTypes;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.List;
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

    @Bean
    public OpenApiCustomizer addRequestBodyExamples() {
        return openApi -> {
            for (var path : openApi.getPaths().entrySet()) {
                logger.debug("{}: {}", path.getKey(), path.getValue().readOperationsMap().keySet());
                path.getValue().readOperations().stream()
                        .filter(operation -> operation.getRequestBody() != null)
                        .map(operation -> operation.getRequestBody().getContent().get(MediaType.APPLICATION_JSON_VALUE))
                        .forEach(mediaType -> mediaType.addExamples("Valid data", ExampleData.getExample(path.getKey(), ExampleTypes.VALID))
                                                       .addExamples("Invalid data", ExampleData.getExample(path.getKey(), ExampleTypes.INVALID)));
            }
        };
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
