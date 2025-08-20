package com.transporter.assignment.service.impl;

import com.transporter.assignment.algorithm.OptimizationException;
import com.transporter.assignment.algorithm.OptimizationParameters;
import com.transporter.assignment.algorithm.OptimizationResult;
import com.transporter.assignment.algorithm.TransporterAssignmentOptimizer;
import com.transporter.assignment.algorithm.ValidationResult;
import com.transporter.assignment.dto.AssignmentRequest;
import com.transporter.assignment.dto.AssignmentResponse;
import com.transporter.assignment.model.Assignment;
import com.transporter.assignment.model.Lane;
import com.transporter.assignment.model.LaneQuote;
import com.transporter.assignment.model.Transporter;
import com.transporter.assignment.repository.LaneQuoteRepository;
import com.transporter.assignment.repository.LaneRepository;
import com.transporter.assignment.repository.TransporterRepository;
import com.transporter.assignment.service.AssignmentException;
import com.transporter.assignment.service.OptimizationCapabilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AssignmentServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private LaneRepository laneRepository;

    @Mock
    private TransporterRepository transporterRepository;

    @Mock
    private LaneQuoteRepository laneQuoteRepository;

    @Mock
    private TransporterAssignmentOptimizer optimizer;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private List<Lane> testLanes;
    private List<LaneQuote> testQuotes;
    private AssignmentRequest validRequest;

    @BeforeEach
    void setUp() {
        // Create test data
        testLanes = List.of(
                new Lane(1L, "Mumbai", "Delhi"),
                new Lane(2L, "Chennai", "Bangalore")
        );

        Transporter t1 = new Transporter(1L, "T1");
        Transporter t2 = new Transporter(2L, "T2");

        testQuotes = List.of(
                new LaneQuote(t1, testLanes.get(0), new BigDecimal("5000")),
                new LaneQuote(t1, testLanes.get(1), new BigDecimal("7000")),
                new LaneQuote(t2, testLanes.get(0), new BigDecimal("5500")),
                new LaneQuote(t2, testLanes.get(1), new BigDecimal("6500"))
        );

        validRequest = new AssignmentRequest(2);
    }

    @Test
    void shouldOptimizeAssignmentSuccessfully() throws AssignmentException, OptimizationException {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(4L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(testQuotes);

        List<Assignment> assignments = List.of(
                new Assignment(1L, 1L, new BigDecimal("5000")),
                new Assignment(2L, 2L, new BigDecimal("6500"))
        );
        OptimizationResult optimizationResult = new OptimizationResult(
                assignments, new BigDecimal("11500"), List.of(1L, 2L));

        when(optimizer.optimize(anyList(), anyList(), any(OptimizationParameters.class)))
                .thenReturn(optimizationResult);

        // When
        AssignmentResponse response = assignmentService.optimizeAssignment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getTotalCost()).isEqualTo(new BigDecimal("11500"));
        assertThat(response.getAssignments()).hasSize(2);
        assertThat(response.getSelectedTransporters()).containsExactly(1L, 2L);

        verify(optimizer).optimize(eq(testLanes), eq(testQuotes), any(OptimizationParameters.class));
    }

    @Test
    void shouldReturnErrorWhenNoInputData() throws AssignmentException {
        // Given
        when(laneRepository.count()).thenReturn(0L);

        // When
        AssignmentResponse response = assignmentService.optimizeAssignment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("No input data available");
    }

    @Test
    void shouldReturnErrorWhenNoLanes() throws AssignmentException {
        // Given
        when(laneRepository.count()).thenReturn(1L);
        when(transporterRepository.count()).thenReturn(1L);
        when(laneQuoteRepository.count()).thenReturn(1L);
        when(laneRepository.findAll()).thenReturn(List.of());

        // When
        AssignmentResponse response = assignmentService.optimizeAssignment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("No lanes available");
    }

    @Test
    void shouldReturnErrorWhenNoQuotes() throws AssignmentException {
        // Given
        when(laneRepository.count()).thenReturn(1L);
        when(transporterRepository.count()).thenReturn(1L);
        when(laneQuoteRepository.count()).thenReturn(1L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(List.of());

        // When
        AssignmentResponse response = assignmentService.optimizeAssignment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("No quotes available");
    }

    @Test
    void shouldReturnFailureWhenOptimizationInfeasible() throws AssignmentException, OptimizationException {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(4L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(testQuotes);

        OptimizationResult infeasibleResult = OptimizationResult.infeasible("No solution found");
        when(optimizer.optimize(anyList(), anyList(), any(OptimizationParameters.class)))
                .thenReturn(infeasibleResult);

        // When
        AssignmentResponse response = assignmentService.optimizeAssignment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("failure");
        assertThat(response.getMessage()).contains("No solution found");
    }

    @Test
    void shouldThrowExceptionWhenOptimizerFails() throws OptimizationException {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(4L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(testQuotes);

        when(optimizer.optimize(anyList(), anyList(), any(OptimizationParameters.class)))
                .thenThrow(new OptimizationException("Algorithm failed"));

        // When/Then
        assertThatThrownBy(() -> assignmentService.optimizeAssignment(validRequest))
                .isInstanceOf(AssignmentException.class)
                .hasMessageContaining("Optimization failed");
    }

    @Test
    void shouldValidateAssignmentSuccessfully() {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(4L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(testQuotes);

        when(optimizer.validate(anyList(), anyList(), any(OptimizationParameters.class)))
                .thenReturn(ValidationResult.valid());

        // When
        AssignmentResponse response = assignmentService.validateAssignment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).contains("validation passed");
    }

    @Test
    void shouldReturnErrorWhenValidationFails() {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(4L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(testQuotes);

        when(optimizer.validate(anyList(), anyList(), any(OptimizationParameters.class)))
                .thenReturn(ValidationResult.invalid(List.of("Validation error")));

        // When
        AssignmentResponse response = assignmentService.validateAssignment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("Validation failed");
    }

    @Test
    void shouldGetOptimizationCapabilitiesSuccessfully() {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(4L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(testQuotes);

        // When
        OptimizationCapabilities capabilities = assignmentService.getOptimizationCapabilities();

        // Then
        assertThat(capabilities.canOptimize()).isTrue();
        assertThat(capabilities.getMaxPossibleTransporters()).isEqualTo(2);
        assertThat(capabilities.getMinRequiredTransporters()).isEqualTo(1);
    }

    @Test
    void shouldReturnNotPossibleWhenNoData() {
        // Given
        when(laneRepository.count()).thenReturn(0L);

        // When
        OptimizationCapabilities capabilities = assignmentService.getOptimizationCapabilities();

        // Then
        assertThat(capabilities.canOptimize()).isFalse();
        assertThat(capabilities.getLimitations()).contains("No input data available");
    }

    @Test
    void shouldReturnNotPossibleWhenLanesUncovered() {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(1L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        
        // Only one quote for lane 1, lane 2 has no quotes
        List<LaneQuote> partialQuotes = List.of(testQuotes.get(0));
        when(laneQuoteRepository.findAll()).thenReturn(partialQuotes);

        // When
        OptimizationCapabilities capabilities = assignmentService.getOptimizationCapabilities();

        // Then
        assertThat(capabilities.canOptimize()).isFalse();
        assertThat(capabilities.getLimitations().get(0)).contains("Some lanes have no quotes");
    }

    @Test
    void shouldHandleValidationWithWarnings() {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(4L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(testQuotes);

        when(optimizer.validate(anyList(), anyList(), any(OptimizationParameters.class)))
                .thenReturn(ValidationResult.validWithWarnings(List.of("Performance warning")));

        // When
        AssignmentResponse response = assignmentService.validateAssignment(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).contains("Warnings: Performance warning");
    }

    @Test
    void shouldReturnErrorForInvalidTransporterCount() {
        // Given
        when(laneRepository.count()).thenReturn(2L);
        when(transporterRepository.count()).thenReturn(2L);
        when(laneQuoteRepository.count()).thenReturn(4L);
        when(laneRepository.findAll()).thenReturn(testLanes);
        when(laneQuoteRepository.findAll()).thenReturn(testQuotes);

        AssignmentRequest invalidRequest = new AssignmentRequest(10); // Too many transporters

        // When
        AssignmentResponse response = assignmentService.validateAssignment(invalidRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("Invalid number of transporters");
    }
}
