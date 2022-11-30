package com.moonstoneid.milkygreen.api;

import com.moonstoneid.milkygreen.AppConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Milky Green API")
                        .description("REST Web Services for Milky Green"))
                .components(new Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(AppConstants.API_KEY_HEADER)));
    }

    @Bean
    public OpenApiCustomiser apiCustomiser() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation -> {
                ApiResponses apiResponses = operation.getResponses();
                addApiErrorResponse(apiResponses, HttpStatus.UNAUTHORIZED);
            }));
        };
    }

    private static void addApiErrorResponse(ApiResponses apiResponses, HttpStatus status) {
        apiResponses.addApiResponse(Integer.toString(status.value()), createApiErrorResponse(
                status.getReasonPhrase()));
    }

    private static ApiResponse createApiErrorResponse(String message) {
        String mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

        MediaType item = new MediaType()
                .schema(new Schema()
                        .name("ErrorResponse")
                        .$ref("#/components/schemas/ErrorResponse"));

        Content content = new Content().addMediaType(mediaType, item);

        return new ApiResponse()
                .description(message)
                .content(content);
    }

}
