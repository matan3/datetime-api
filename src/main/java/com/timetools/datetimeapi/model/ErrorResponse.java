package com.timetools.datetimeapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Error message describing what went wrong", example = "Invalid datetime format")
    private String message;

    @Schema(description = "HTTP status code", example = "400")
    private int status;
}
