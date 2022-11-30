package com.moonstoneid.milkygreen.api.doc;

import com.moonstoneid.milkygreen.api.model.ErrorResponseAM;
import com.moonstoneid.milkygreen.api.model.GeneralSettingsAM;
import com.moonstoneid.milkygreen.api.model.KeyPairAM;
import com.moonstoneid.milkygreen.api.model.UpdateGeneralSettingsAM;
import com.moonstoneid.milkygreen.api.model.UpdateKeyPairAM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "settings", description = "Settings API")
public interface SettingsApi {

    @Operation(
        summary = "Get general settings",
        tags = { "settings" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = GeneralSettingsAM.class))
        )
    })
    GeneralSettingsAM getGeneralSettings();

    @Operation(
        summary = "Update general settings",
        tags = { "settings" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = GeneralSettingsAM.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        )
    })
    GeneralSettingsAM updateGeneralSettings(
            @Parameter(required = true) UpdateGeneralSettingsAM updateGeneralSettings);

    @Operation(
        summary = "Get token signing keypair",
        tags = { "settings" },
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
        summary = "Update token signing keypair",
        tags = { "settings" },
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
