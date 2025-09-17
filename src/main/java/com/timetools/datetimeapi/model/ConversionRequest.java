package com.timetools.datetimeapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ConversionRequest {

    @Schema(example = "2025-09-17T12:00:00", description = "The datetime to convert in ISO format")
    private String datetime;

    @Schema(example = "UTC", description = "Source timezone")
    private String fromTimezone;

    @Schema(example = "Asia/Jerusalem", description = "Target timezone")
    private String toTimezone;
}
