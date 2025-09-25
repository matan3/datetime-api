package com.timetools.datetimeapi.service;

import com.timetools.datetimeapi.model.ConversionLog;
import com.timetools.datetimeapi.repository.ConversionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConvertServiceTest {

    @Mock
    private ConversionLogRepository repository;

    @InjectMocks
    private ConvertService convertService;

    private static final String CONVERTED_DATE_TIME = "convertedDatetime";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertTimezone_validInput() {
        String datetime = "2025-01-01T10:00:00";
        String fromTz = "UTC";
        String toTz = "Asia/Jerusalem";

        Map<String, String> resultMap = convertService.convertTimezone(datetime, fromTz, toTz);
        String result = resultMap.get(CONVERTED_DATE_TIME);

        assertNotNull(result);
        assertTrue(result.contains("+02:00"));

        ArgumentCaptor<ConversionLog> captor = ArgumentCaptor.forClass(ConversionLog.class);
        verify(repository, times(1)).save(captor.capture());
        ConversionLog saved = captor.getValue();

        assertEquals(datetime, saved.getInputDatetime());
        assertEquals(fromTz, saved.getFromTimezone());
        assertEquals(toTz, saved.getToTimezone());
        assertEquals(result, saved.getOutputDatetime());
    }

    @Test
    void testConvertTimezone_sameTimezone() {
        String datetime = "2025-01-01T10:00:00";
        String fromTz = "Asia/Jerusalem";
        String toTz = "Asia/Jerusalem";

        Map<String, String> resultMap = convertService.convertTimezone(datetime, fromTz, toTz);
        String result = resultMap.get(CONVERTED_DATE_TIME);

        assertNotNull(result);
        assertTrue(result.endsWith("+02:00"));

        ArgumentCaptor<ConversionLog> captor = ArgumentCaptor.forClass(ConversionLog.class);
        verify(repository, times(1)).save(captor.capture());
        ConversionLog saved = captor.getValue();

        assertEquals(datetime, saved.getInputDatetime());
        assertEquals(fromTz, saved.getFromTimezone());
        assertEquals(toTz, saved.getToTimezone());
        assertEquals(result, saved.getOutputDatetime());
    }

    @Test
    void testConvertTimezone_invalidDatetime() {
        String invalidDatetime = "not-a-datetime";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> convertService.convertTimezone(invalidDatetime, "UTC", "Asia/Jerusalem"));

        assertTrue(ex.getMessage().contains("Datetime must be in ISO_LOCAL_DATE_TIME format"));
        verify(repository, never()).save(any());
    }

    @Test
    void testConvertTimezone_emptyDatetime() {
        String emptyDatetime = "";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> convertService.convertTimezone(emptyDatetime, "UTC", "Asia/Tokyo"));

        assertTrue(ex.getMessage().contains("Datetime cannot be empty"));
        verify(repository, never()).save(any());
    }

    @Test
    void testConvertTimezone_invalidFromTimezone() {
        String datetime = "2025-01-01T10:00:00";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> convertService.convertTimezone(datetime, "INVALID", "UTC"));

        assertTrue(ex.getMessage().contains("Invalid fromTimezone"));
        verify(repository, never()).save(any());
    }

    @Test
    void testConvertTimezone_invalidToTimezone() {
        String datetime = "2025-01-01T10:00:00";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> convertService.convertTimezone(datetime, "UTC", "INVALID"));

        assertTrue(ex.getMessage().contains("Invalid toTimezone"));
        verify(repository, never()).save(any());
    }

    @Test
    void testConvertTimezone_emptyFromTimezone() {
        String datetime = "2025-01-01T10:00:00";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> convertService.convertTimezone(datetime, "", "UTC"));

        assertTrue(ex.getMessage().contains("Timezones cannot be empty"));
        verify(repository, never()).save(any());
    }

    @Test
    void testConvertTimezone_emptyToTimezone() {
        String datetime = "2025-01-01T10:00:00";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> convertService.convertTimezone(datetime, "UTC", ""));

        assertTrue(ex.getMessage().contains("Timezones cannot be empty"));
        verify(repository, never()).save(any());
    }

}
