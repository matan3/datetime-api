package com.timetools.datetimeapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "conversion_log")
public class ConversionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID of the conversion", example = "1")
    private Long id;

    @Column(name = "input_datetime", nullable = false)
    @Schema(description = "Input datetime", example = "2025-09-17T12:00:00")
    private String inputDatetime;

    @Column(name = "from_timezone", nullable = false)
    @Schema(description = "Source timezone", example = "UTC")
    private String fromTimezone;

    @Column(name = "to_timezone", nullable = false)
    @Schema(description = "Target timezone", example = "Asia/Jerusalem")
    private String toTimezone;

    @Column(name = "output_datetime", nullable = false)
    @Schema(description = "Converted datetime with offset", example = "2025-09-17T15:00:00+03:00")
    private String outputDatetime;

    @Column(name = "created_at")
    @Schema(description = "Timestamp when conversion was saved", example = "2025-09-17T14:05:30.123")
    private LocalDateTime createdAt = LocalDateTime.now();
}
