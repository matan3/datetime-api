package com.timetools.datetimeapi.service;

import com.timetools.datetimeapi.model.ConversionLog;
import com.timetools.datetimeapi.repository.ConversionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConvertServiceTest {

    private ConversionLogRepository repository;
    private ConvertService convertService;

    @BeforeEach
    void setUp() {
        repository = mock(ConversionLogRepository.class);
        convertService = new ConvertService(repository);
    }

    @Test
    void testConvertTimezone_validInput() {
        // given
        String datetime = "2025-01-01T10:00:00";
        String fromTz = "UTC";
        String toTz = "Asia/Jerusalem";

        // when
        String result = convertService.convertTimezone(datetime, fromTz, toTz);

        // then
        assertNotNull(result);
        assertTrue(result.contains("+02:00") || result.contains("+03:00")); // handles DST

        // verify repo save
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
        // given
        String invalidDatetime = "not-a-datetime";

        // when / then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> convertService.convertTimezone(invalidDatetime, "UTC", "Asia/Jerusalem"));

        assertTrue(ex.getMessage().contains("Invalid input"));
        verify(repository, never()).save(any());
    }

    @Test
    void testConvertTimezone_invalidTimezone() {
        // given
        String datetime = "2025-01-01T10:00:00";

        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> convertService.convertTimezone(datetime, "INVALID", "UTC"));

        verify(repository, never()).save(any());
    }
}
