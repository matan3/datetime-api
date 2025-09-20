package com.timetools.datetimeapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timetools.datetimeapi.model.ConversionLog;
import com.timetools.datetimeapi.model.ConversionRequest;
import com.timetools.datetimeapi.repository.ConversionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class ConvertIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("datetime_test_db")
            .withUsername("root")
            .withPassword("admin");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversionLogRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll(); // clean DB before each test
    }

    @Test
    void shouldConvertDatetimeAndSaveToDatabase() throws Exception {
        ConversionRequest request = new ConversionRequest();
        request.setDatetime("2025-09-19T12:00:00");
        request.setFromTimezone("UTC");
        request.setToTimezone("Europe/London");

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<ConversionLog> logs = repository.findAll();
        assertThat(logs).hasSize(1);

        ConversionLog log = logs.get(0);
        assertThat(log.getInputDatetime()).isEqualTo("2025-09-19T12:00:00");
        assertThat(log.getFromTimezone()).isEqualTo("UTC");
        assertThat(log.getToTimezone()).isEqualTo("Europe/London");
        assertThat(log.getOutputDatetime()).contains("+01:00");
    }

    @Test
    void shouldFailOnInvalidDatetime() throws Exception {
        ConversionRequest request = new ConversionRequest();
        request.setDatetime("invalid-date");
        request.setFromTimezone("UTC");
        request.setToTimezone("Europe/London");

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void shouldFailOnInvalidTimezone() throws Exception {
        ConversionRequest request = new ConversionRequest();
        request.setDatetime("2025-09-19T12:00:00");
        request.setFromTimezone("INVALID_ZONE");
        request.setToTimezone("Europe/London");

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void shouldSaveMultipleConversions() throws Exception {
        ConversionRequest request1 = new ConversionRequest();
        request1.setDatetime("2025-09-19T10:00:00");
        request1.setFromTimezone("UTC");
        request1.setToTimezone("Asia/Jerusalem");

        ConversionRequest request2 = new ConversionRequest();
        request2.setDatetime("2025-09-19T15:00:00");
        request2.setFromTimezone("UTC");
        request2.setToTimezone("Europe/London");

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        List<ConversionLog> logs = repository.findAll();
        assertThat(logs).hasSize(2);
    }
}
