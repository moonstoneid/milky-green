package com.moonstoneid.milkygreen.api.doc;

import java.util.List;

import com.moonstoneid.milkygreen.api.model.ClientAM;
import com.moonstoneid.milkygreen.api.model.CreateClientAM;
import com.moonstoneid.milkygreen.api.model.ErrorResponseAM;
import com.moonstoneid.milkygreen.api.model.UpdateClientAM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "clients", description = "OAuth clients API")
public interface ClientsApi {

    @Operation(
        summary = "List all OAuth clients",
        tags = { "clients" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = ClientAM.class))
            )
        )
    })
    List<ClientAM> getClients();

    @Operation(
        summary = "Create a new OAuth client",
        tags = { "clients" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = ClientAM.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        )
    })
    ClientAM createClient(@Parameter(required = true) CreateClientAM createClient);

    @Operation(
        summary = "Get an OAuth client by its ID",
        tags = { "clients" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = ClientAM.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        )
    })
    ClientAM getClient(@Parameter(description = "id", required = true) String id);

    @Operation(
        summary = "Update an OAuth client by its ID",
        tags = { "clients" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = ClientAM.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        )
    })
    ClientAM updateClient(@Parameter(description = "id", required = true) String id,
            @Parameter(required = true) UpdateClientAM updateClient);

    @Operation(
        summary = "Delete an OAuth client by its ID",
        tags = { "clients" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "No content"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        )
    })
    void deleteClient(@Parameter(description = "id", required = true) String id);

}
