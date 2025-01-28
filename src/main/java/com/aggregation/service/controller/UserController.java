package com.aggregation.service.controller;

import com.aggregation.service.model.User;
import com.aggregation.service.service.UserAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User aggregation endpoints")
@RequiredArgsConstructor
public class UserController {
    private final UserAggregationService userAggregationService;

    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieves users from all configured data sources and aggregates them. Supports filtering by username or name/surname.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved users",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = User.class))
                )
            )
        }
    )
    public ResponseEntity<List<User>> getUsers(
            @Parameter(description = "Filter by username")
            @RequestParam(required = false) String username,
            @Parameter(description = "Filter by name or surname")
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(userAggregationService.searchUsers(username, name));
    }

    @PostMapping
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user in the PostgreSQL database",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully created user",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class)
                )
            )
        }
    )
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setId(UUID.randomUUID().toString());
        return ResponseEntity.ok(userAggregationService.createUser(user));
    }
}
