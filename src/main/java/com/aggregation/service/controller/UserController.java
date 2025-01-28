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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User aggregation endpoints")
public class UserController {
    private final UserAggregationService userAggregationService;

    public UserController(UserAggregationService userAggregationService) {
        this.userAggregationService = userAggregationService;
    }

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
}
