package com.aggregation.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response containing details about the failure")
public class Error {
    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error message describing what went wrong", example = "Invalid user data provided")
    private String message;

    @Schema(description = "Detailed error description or technical details", example = "Username field cannot be empty")
    private String details;

    @Schema(description = "Timestamp when the error occurred", example = "2025-01-29T00:17:15+02:00")
    private String timestamp;
}
