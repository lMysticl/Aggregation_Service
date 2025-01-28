package com.aggregation.service.controller;

import com.aggregation.service.model.User;
import com.aggregation.service.service.UserAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User aggregation API for managing users across multiple databases")
@RequiredArgsConstructor
public class UserController {
    private final UserAggregationService userAggregationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get users with filters",
        description = """
            Retrieves users from all configured data sources (PostgreSQL and MongoDB) and aggregates them.
            Supports optional filtering by username or name/surname.
            If no filters are provided, returns all users from both databases.""",
        tags = {"Users"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved users",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = User.class)),
                examples = @ExampleObject(
                    value = """
                        [{
                          "id": "123e4567-e89b-12d3-a456-426614174000",
                          "username": "johndoe",
                          "name": "John",
                          "surname": "Doe"
                        }]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred while fetching users",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Error.class)
            )
        )
    })
    public ResponseEntity<List<User>> getUsers(
            @Parameter(
                name = "username",
                description = "Filter users by username (case-insensitive, partial match)",
                example = "john",
                in = ParameterIn.QUERY
            )
            @RequestParam(required = false) String username,
            @Parameter(
                name = "name",
                description = "Filter users by name or surname (case-insensitive, partial match)",
                example = "doe",
                in = ParameterIn.QUERY
            )
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(userAggregationService.searchUsers(username, name));
    }

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Create a new user",
        description = """
            Creates a new user in the PostgreSQL database.
            The user ID will be automatically generated if not provided.
            Username must be unique across the system.""",
        tags = {"Users"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User successfully created",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "id": "123e4567-e89b-12d3-a456-426614174000",
                          "username": "johndoe",
                          "name": "John",
                          "surname": "Doe"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid user data provided",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Error.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username already exists",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Error.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred while creating user",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Error.class)
            )
        )
    })
    public ResponseEntity<User> createUser(
            @Parameter(
                description = "User data to create",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(
                        value = """
                            {
                              "username": "johndoe",
                              "name": "John",
                              "surname": "Doe"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody User user) {
        user.setId(UUID.randomUUID().toString());
        return ResponseEntity.ok(userAggregationService.createUser(user));
    }
}
