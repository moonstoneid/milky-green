package com.moonstoneid.web3login.api.doc;


import com.moonstoneid.web3login.api.model.ErrorResponseAM;
import com.moonstoneid.web3login.api.model.KeyPairAM;
import com.moonstoneid.web3login.api.model.UpdateKeyPairAM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "keypair", description = "KeyPair API")
public interface KeyPairApi {

    @Operation(
        summary = "Get the keypair which is used to sign OIDC claims",
        tags = { "keypair" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = KeyPairAM.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        )
    })
    KeyPairAM getKeyPair();

    @Operation(
        summary = "Update keypair",
        tags = { "keypair" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = KeyPairAM.class))
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
    KeyPairAM updateKeyPair(@Parameter(required = true) UpdateKeyPairAM updateKeyPair);

}
