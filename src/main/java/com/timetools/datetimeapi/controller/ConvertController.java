package com.timetools.datetimeapi.controller;

import com.timetools.datetimeapi.model.ConversionLog;
import com.timetools.datetimeapi.model.ConversionRequest;
import com.timetools.datetimeapi.repository.ConversionLogRepository;
import com.timetools.datetimeapi.service.ConvertService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Map<String, String> convert(@RequestBody ConversionRequest request) {
        return convertService.convertTimezone(
                request.getDatetime(),
                request.getFromTimezone(),
                request.getToTimezone()
        );
    }

    // GET /api/conversions
    @GetMapping("/conversions")
    public List<ConversionLog> getAllConversions() {
        return repository.findAll();
    }
}
