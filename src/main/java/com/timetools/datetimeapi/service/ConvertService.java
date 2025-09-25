package com.timetools.datetimeapi.service;

import com.timetools.datetimeapi.model.ConversionLog;
import com.timetools.datetimeapi.repository.ConversionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        final String output;
        try {
            // Validate datetime format
            if (datetime == null || datetime.isBlank()) {
                throw new IllegalArgumentException("Datetime cannot be empty");
            }
            LocalDateTime localDateTime;
            try {
                localDateTime = LocalDateTime.parse(datetime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Datetime must be in ISO_LOCAL_DATE_TIME format (yyyy-MM-ddTHH:mm:ss)");
            }

            // Validate timezones
            if (fromTimezone == null || toTimezone == null || fromTimezone.isBlank() || toTimezone.isBlank()) {
                throw new IllegalArgumentException("Timezones cannot be empty");
            }
            if (!ZoneId.getAvailableZoneIds().contains(fromTimezone)) {
                throw new IllegalArgumentException("Invalid fromTimezone: " + fromTimezone);
            }
            if (!ZoneId.getAvailableZoneIds().contains(toTimezone)) {
                throw new IllegalArgumentException("Invalid toTimezone: " + toTimezone);
            }

            // If same timezone, return original datetime with offset
            if (fromTimezone.equals(toTimezone)) {
                ZonedDateTime sameZone = localDateTime.atZone(ZoneId.of(fromTimezone));
                output = sameZone.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } else {
                ZonedDateTime sourceZoned = localDateTime.atZone(ZoneId.of(fromTimezone));
                ZonedDateTime targetZoned = sourceZoned.withZoneSameInstant(ZoneId.of(toTimezone));
                output = targetZoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
            // TODO: Handle DST transitions properly (ambiguous or missing times)

            // Save to database
            ConversionLog log = new ConversionLog();
            log.setInputDatetime(datetime);
            log.setFromTimezone(fromTimezone);
            log.setToTimezone(toTimezone);
            log.setOutputDatetime(output);
            repository.save(log);

            // Return JSON
            return Map.of(CONVERTED_DATE_TIME, output);

        } catch (IllegalArgumentException e) {
            throw e; // pass to controller to handle
        } catch (Exception e) {
            throw new IllegalArgumentException("Unexpected error: " + e.getMessage());
        }
    }

}
