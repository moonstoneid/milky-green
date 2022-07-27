package org.mn.web3login.api.doc;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mn.web3login.api.model.Client;
import org.mn.web3login.api.model.ErrorResponse;

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
                array = @ArraySchema(schema = @Schema(implementation = Client.class))
            )
        )
    })
    List<Client> getClients();

    @Operation(
        summary = "Get an OAuth client by its ID",
        tags = { "clients" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = Client.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    Client getClient(@Parameter(description = "id", required = true) String id);

}
