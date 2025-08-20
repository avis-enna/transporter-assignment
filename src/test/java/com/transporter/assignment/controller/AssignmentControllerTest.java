package com.transporter.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transporter.assignment.dto.AssignmentDto;
import com.transporter.assignment.dto.AssignmentRequest;
import com.transporter.assignment.dto.AssignmentResponse;
import com.transporter.assignment.service.AssignmentException;
import com.transporter.assignment.service.AssignmentService;
import com.transporter.assignment.service.OptimizationCapabilities;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AssignmentController.
 */
@WebMvcTest(AssignmentController.class)
@ActiveProfiles("test")
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignmentService assignmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private AssignmentRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new AssignmentRequest(3);
    }

    @Test
    void shouldOptimizeAssignmentSuccessfully() throws Exception {
        // Given
        List<AssignmentDto> assignments = List.of(
                new AssignmentDto(1L, 1L, new BigDecimal("5000")),
                new AssignmentDto(2L, 2L, new BigDecimal("7000"))
        );
        AssignmentResponse successResponse = AssignmentResponse.success(
                new BigDecimal("12000"), assignments, List.of(1L, 2L));

        when(assignmentService.optimizeAssignment(any(AssignmentRequest.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.totalCost").value(12000))
                .andExpect(jsonPath("$.assignments").isArray())
                .andExpect(jsonPath("$.assignments[0].laneId").value(1))
                .andExpect(jsonPath("$.assignments[0].transporterId").value(1))
                .andExpect(jsonPath("$.selectedTransporters").isArray())
                .andExpect(jsonPath("$.selectedTransporters[0]").value(1));

        verify(assignmentService).optimizeAssignment(any(AssignmentRequest.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidMaxTransporters() throws Exception {
        // Given - Invalid request with zero max transporters
        AssignmentRequest invalidRequest = new AssignmentRequest(0);

        // When & Then
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(assignmentService, never()).optimizeAssignment(any());
    }

    @Test
    void shouldHandleAssignmentException() throws Exception {
        // Given
        when(assignmentService.optimizeAssignment(any(AssignmentRequest.class)))
                .thenThrow(new AssignmentException("No input data available"));

        // When & Then
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("No input data available"));
    }

    @Test
    void shouldReturnFailureForInfeasibleOptimization() throws Exception {
        // Given
        AssignmentResponse failureResponse = AssignmentResponse.failure("No feasible solution found");

        when(assignmentService.optimizeAssignment(any(AssignmentRequest.class)))
                .thenReturn(failureResponse);

        // When & Then
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("failure"))
                .andExpect(jsonPath("$.message").value("No feasible solution found"));
    }

    @Test
    void shouldValidateAssignmentSuccessfully() throws Exception {
        // Given
        AssignmentResponse validationResponse = AssignmentResponse.success(null, List.of(), List.of())
                .withMessage("Validation passed");

        when(assignmentService.validateAssignment(any(AssignmentRequest.class)))
                .thenReturn(validationResponse);

        // When & Then
        mockMvc.perform(post("/transporters/assignment/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Validation passed"));

        verify(assignmentService).validateAssignment(any(AssignmentRequest.class));
    }

    @Test
    void shouldReturnValidationErrors() throws Exception {
        // Given
        AssignmentResponse errorResponse = AssignmentResponse.error("Invalid number of transporters");

        when(assignmentService.validateAssignment(any(AssignmentRequest.class)))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(post("/transporters/assignment/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid number of transporters"));
    }

    @Test
    void shouldGetOptimizationCapabilities() throws Exception {
        // Given
        OptimizationCapabilities capabilities = OptimizationCapabilities.possible(5, 2);

        when(assignmentService.getOptimizationCapabilities()).thenReturn(capabilities);

        // When & Then
        mockMvc.perform(get("/transporters/assignment/capabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canOptimize").value(true))
                .andExpect(jsonPath("$.maxPossibleTransporters").value(5))
                .andExpect(jsonPath("$.minRequiredTransporters").value(2));

        verify(assignmentService).getOptimizationCapabilities();
    }

    @Test
    void shouldReturnCapabilitiesWhenOptimizationNotPossible() throws Exception {
        // Given
        OptimizationCapabilities capabilities = OptimizationCapabilities.notPossible("No input data");

        when(assignmentService.getOptimizationCapabilities()).thenReturn(capabilities);

        // When & Then
        mockMvc.perform(get("/transporters/assignment/capabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canOptimize").value(false))
                .andExpect(jsonPath("$.limitations[0]").value("No input data"));
    }

    @Test
    void shouldPerformQuickOptimization() throws Exception {
        // Given
        List<AssignmentDto> assignments = List.of(
                new AssignmentDto(1L, 1L, new BigDecimal("5000"))
        );
        AssignmentResponse successResponse = AssignmentResponse.success(
                new BigDecimal("5000"), assignments, List.of(1L));

        when(assignmentService.optimizeAssignment(any(AssignmentRequest.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/transporters/assignment/quick")
                        .param("maxTransporters", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.totalCost").value(5000));

        verify(assignmentService).optimizeAssignment(any(AssignmentRequest.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidQuickOptimizationParams() throws Exception {
        // When & Then
        mockMvc.perform(post("/transporters/assignment/quick")
                        .param("maxTransporters", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnHealthCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/transporters/assignment/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Assignment service is healthy"));
    }

    @Test
    void shouldHandleComplexAssignmentRequest() throws Exception {
        // Given - Request with all parameters
        AssignmentRequest complexRequest = new AssignmentRequest(5, true, true, true, 60);
        
        List<AssignmentDto> assignments = List.of(
                new AssignmentDto(1L, 1L, new BigDecimal("5000")),
                new AssignmentDto(2L, 2L, new BigDecimal("7000")),
                new AssignmentDto(3L, 3L, new BigDecimal("6000"))
        );
        AssignmentResponse successResponse = AssignmentResponse.success(
                new BigDecimal("18000"), assignments, List.of(1L, 2L, 3L));

        when(assignmentService.optimizeAssignment(any(AssignmentRequest.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complexRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.totalCost").value(18000))
                .andExpect(jsonPath("$.assignments").isArray())
                .andExpect(jsonPath("$.assignments").value(hasSize(3)))
                .andExpect(jsonPath("$.selectedTransporters").value(hasSize(3)));
    }

    @Test
    void shouldHandleMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleExcessiveMaxTransporters() throws Exception {
        // Given - Request with too many transporters
        AssignmentRequest excessiveRequest = new AssignmentRequest(150); // Over the limit

        // When & Then
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(excessiveRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
}
