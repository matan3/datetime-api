package com.timetools.datetimeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timetools.datetimeapi.model.ConversionLog;
import com.timetools.datetimeapi.model.ConversionRequest;
import com.timetools.datetimeapi.repository.ConversionLogRepository;
import com.timetools.datetimeapi.service.ConvertService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConvertController.class)
class ConvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConvertService convertService;

    @MockBean
    private ConversionLogRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testConvertEndpoint() throws Exception {
        ConversionRequest request = new ConversionRequest();
        request.setDatetime("2025-09-19T12:00:00");
        request.setFromTimezone("UTC");
        request.setToTimezone("Europe/Tel_Aviv");

        Mockito.when(convertService.convertTimezone(any(), any(), any()))
                .thenReturn("2025-09-19T15:00:00+03:00");

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("2025-09-19T15:00:00+03:00"));
    }

    @Test
    void testGetAllConversionsEndpoint() throws Exception {
        ConversionLog log1 = new ConversionLog();
        log1.setInputDatetime("2025-09-19T12:00:00");
        log1.setFromTimezone("UTC");
        log1.setToTimezone("Europe/Tel_Aviv");
        log1.setOutputDatetime("2025-09-19T15:00:00+03:00");

        ConversionLog log2 = new ConversionLog();
        log2.setInputDatetime("2025-09-20T09:00:00");
        log2.setFromTimezone("UTC");
        log2.setToTimezone("Europe/London");
        log2.setOutputDatetime("2025-09-20T10:00:00+01:00");

        Mockito.when(repository.findAll()).thenReturn(Arrays.asList(log1, log2));

        mockMvc.perform(get("/api/conversions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fromTimezone").value("UTC"))
                .andExpect(jsonPath("$[1].toTimezone").value("Europe/London"));
    }
}
