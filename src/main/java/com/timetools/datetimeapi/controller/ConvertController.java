package com.timetools.datetimeapi.controller;

import com.timetools.datetimeapi.model.ConversionLog;
import com.timetools.datetimeapi.model.ConversionRequest;
import com.timetools.datetimeapi.repository.ConversionLogRepository;
import com.timetools.datetimeapi.service.ConvertService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Datetime Conversion", description = "Convert datetime between timezones")
public class ConvertController {

    private final ConvertService convertService;
    private final ConversionLogRepository repository;


    @Autowired
    public ConvertController(ConvertService convertService, ConversionLogRepository repository) {
        this.convertService = convertService;
        this.repository = repository;
    }

    @PostMapping("/convert")
    public ResponseEntity<Map<String, String>> convert(@RequestBody ConversionRequest request) {
        try {
            Map<String, String> result = convertService.convertTimezone(
                    request.getDatetime(),
                    request.getFromTimezone(),
                    request.getToTimezone()
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected server error: " + e.getMessage()));
        }
    }

    // GET /api/conversions
    @GetMapping("/conversions")
    public List<ConversionLog> getAllConversions() {
        return repository.findAll();
    }
}
