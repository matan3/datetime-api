package com.timetools.datetimeapi.service;

import com.timetools.datetimeapi.model.ConversionLog;
import com.timetools.datetimeapi.repository.ConversionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class ConvertService {

    private final ConversionLogRepository repository;
    private static final String CONVERTED_DATE_TIME = "convertedDatetime";

    @Autowired
    public ConvertService(ConversionLogRepository repository) {
        this.repository = repository;
    }

    public Map<String, String> convertTimezone(String datetime, String fromTimezone, String toTimezone) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(datetime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            // TODO: Handle DST transitions properly (ambiguous or missing times)
            // TODO: Handle case when fromTimezone.equals(toTimezone) explicitly
            ZonedDateTime sourceZoned = localDateTime.atZone(ZoneId.of(fromTimezone));
            ZonedDateTime targetZoned = sourceZoned.withZoneSameInstant(ZoneId.of(toTimezone));
            String output = targetZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // Save to database
            ConversionLog log = new ConversionLog();
            log.setInputDatetime(datetime);
            log.setFromTimezone(fromTimezone);
            log.setToTimezone(toTimezone);
            log.setOutputDatetime(output);

            repository.save(log);

            // Return JSON
            return Map.of(CONVERTED_DATE_TIME, output);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid input: " + e.getMessage());
        }
    }
}
