package com.transporter.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transporter.assignment.dto.*;
import com.transporter.assignment.service.InputDataException;
import com.transporter.assignment.service.InputDataService;
import com.transporter.assignment.service.InputDataStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for InputDataController.
 */
@WebMvcTest(InputDataController.class)
@ActiveProfiles("test")
class InputDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InputDataService inputDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private InputDataRequest validRequest;

    @BeforeEach
    void setUp() {
        List<LaneDto> lanes = List.of(
                new LaneDto(1L, "Mumbai", "Delhi"),
                new LaneDto(2L, "Chennai", "Bangalore")
        );

        List<TransporterDto> transporters = List.of(
                new TransporterDto(1L, "Transporter T1", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("5000")),
                        new LaneQuoteDto(2L, new BigDecimal("7000"))
                )),
                new TransporterDto(2L, "Transporter T2", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("5500"))
                ))
        );

        validRequest = new InputDataRequest(lanes, transporters);
    }

    @Test
    void shouldSubmitInputDataSuccessfully() throws Exception {
        // Given
        when(inputDataService.saveInputData(any(InputDataRequest.class)))
                .thenReturn(InputDataResponse.success());

        // When & Then
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Input data saved successfully."));

        verify(inputDataService).saveInputData(any(InputDataRequest.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        // Given - Invalid request with null lane ID
        List<LaneDto> invalidLanes = List.of(new LaneDto(null, "Mumbai", "Delhi"));
        InputDataRequest invalidRequest = new InputDataRequest(invalidLanes, validRequest.getTransporters());

        // When & Then
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(inputDataService, never()).saveInputData(any());
    }

    @Test
    void shouldHandleInputDataException() throws Exception {
        // Given
        when(inputDataService.saveInputData(any(InputDataRequest.class)))
                .thenThrow(new InputDataException("Database error"));

        // When & Then
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Database error"));
    }

    @Test
    void shouldValidateInputDataSuccessfully() throws Exception {
        // Given
        when(inputDataService.validateInputData(any(InputDataRequest.class)))
                .thenReturn(InputDataResponse.success("Validation passed"));

        // When & Then
        mockMvc.perform(post("/transporters/input/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(inputDataService).validateInputData(any(InputDataRequest.class));
    }

    @Test
    void shouldReturnValidationErrors() throws Exception {
        // Given
        when(inputDataService.validateInputData(any(InputDataRequest.class)))
                .thenReturn(InputDataResponse.error("Validation failed: Duplicate lane ID"));

        // When & Then
        mockMvc.perform(post("/transporters/input/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Validation failed: Duplicate lane ID"));
    }

    @Test
    void shouldClearInputDataSuccessfully() throws Exception {
        // Given
        when(inputDataService.clearInputData())
                .thenReturn(InputDataResponse.success("Data cleared successfully"));

        // When & Then
        mockMvc.perform(delete("/transporters/input"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(inputDataService).clearInputData();
    }

    @Test
    void shouldGetInputDataStatistics() throws Exception {
        // Given
        InputDataStatistics stats = new InputDataStatistics(5, 3, 12, 5, 3);
        when(inputDataService.getInputDataStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/transporters/input/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.laneCount").value(5))
                .andExpect(jsonPath("$.transporterCount").value(3))
                .andExpect(jsonPath("$.quoteCount").value(12));

        verify(inputDataService).getInputDataStatistics();
    }

    @Test
    void shouldCheckIfInputDataExists() throws Exception {
        // Given
        when(inputDataService.hasInputData()).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/transporters/input/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(inputDataService).hasInputData();
    }

    @Test
    void shouldReturnHealthCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/transporters/input/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Input data service is healthy"));
    }

    @Test
    void shouldHandleEmptyLanesList() throws Exception {
        // Given - Request with empty lanes list
        InputDataRequest emptyLanesRequest = new InputDataRequest(List.of(), validRequest.getTransporters());

        // When & Then
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyLanesRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void shouldHandleEmptyTransportersList() throws Exception {
        // Given - Request with empty transporters list
        InputDataRequest emptyTransportersRequest = new InputDataRequest(validRequest.getLanes(), List.of());

        // When & Then
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyTransportersRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void shouldHandleInvalidQuoteValue() throws Exception {
        // Given - Request with negative quote
        List<TransporterDto> invalidTransporters = List.of(
                new TransporterDto(1L, "T1", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("-1000")) // Negative quote
                ))
        );
        InputDataRequest invalidRequest = new InputDataRequest(validRequest.getLanes(), invalidTransporters);

        // When & Then
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void shouldHandleMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
}
