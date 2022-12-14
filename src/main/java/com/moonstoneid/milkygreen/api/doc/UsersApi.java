package com.moonstoneid.milkygreen.api.doc;

import java.util.List;

import com.moonstoneid.milkygreen.api.model.CreateUserAM;
import com.moonstoneid.milkygreen.api.model.ErrorResponseAM;
import com.moonstoneid.milkygreen.api.model.UserAM;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "users", description = "Users API")
public interface UsersApi {

    @Operation(
        summary = "List all users",
        tags = { "users" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = UserAM.class))
            )
        )
    })
    List<UserAM> getUsers();

    @Operation(
        summary = "Get a user by its username",
        tags = { "users" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = UserAM.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseAM.class))
        )
    })
    UserAM getUser(@Parameter(description = "username", required = true) String username);

    @Operation(
        summary = "Create a new user",
        tags = { "users" },
        security = { @SecurityRequirement(name = "apiKey") }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = UserAM.class))
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
    UserAM createUser(@Parameter(required = true) CreateUserAM createUser);

    @Operation(
        summary = "Delete a user by its username",
        tags = { "users" },
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
    void deleteUser(@Parameter(description = "username", required = true) String username);

    @Operation(
        summary = "Enable a user by its username",
        tags = { "users" },
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
    void enableUser(@Parameter(description = "username", required = true) String username);

    @Operation(
        summary = "Disable a user by its username",
        tags = { "users" },
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
    void disableUser(@Parameter(description = "username", required = true) String username);

}
