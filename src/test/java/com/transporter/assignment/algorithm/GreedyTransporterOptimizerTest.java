package com.transporter.assignment.algorithm;

import com.transporter.assignment.model.Assignment;
import com.transporter.assignment.model.Lane;
import com.transporter.assignment.model.LaneQuote;
import com.transporter.assignment.model.Transporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for GreedyTransporterOptimizer.
 */
class GreedyTransporterOptimizerTest {

    private GreedyTransporterOptimizer optimizer;
    private List<Lane> testLanes;
    private List<LaneQuote> testQuotes;

    @BeforeEach
    void setUp() {
        optimizer = new GreedyTransporterOptimizer();
        setupTestData();
    }

    private void setupTestData() {
        // Create test lanes
        testLanes = List.of(
                new Lane(1L, "Mumbai", "Delhi"),
                new Lane(2L, "Chennai", "Bangalore"),
                new Lane(3L, "Pune", "Hyderabad")
        );

        // Create test transporters
        Transporter t1 = new Transporter(1L, "Transporter T1");
        Transporter t2 = new Transporter(2L, "Transporter T2");
        Transporter t3 = new Transporter(3L, "Transporter T3");

        // Create test quotes
        testQuotes = List.of(
                new LaneQuote(t1, testLanes.get(0), new BigDecimal("5000")),  // T1 -> Lane 1: 5000
                new LaneQuote(t1, testLanes.get(1), new BigDecimal("7000")),  // T1 -> Lane 2: 7000
                new LaneQuote(t2, testLanes.get(0), new BigDecimal("5500")),  // T2 -> Lane 1: 5500
                new LaneQuote(t2, testLanes.get(2), new BigDecimal("6000")),  // T2 -> Lane 3: 6000
                new LaneQuote(t3, testLanes.get(1), new BigDecimal("6500")),  // T3 -> Lane 2: 6500
                new LaneQuote(t3, testLanes.get(2), new BigDecimal("5800"))   // T3 -> Lane 3: 5800
        );
    }

    @Test
    void shouldReturnAlgorithmName() {
        // When
        String name = optimizer.getAlgorithmName();

        // Then
        assertThat(name).isEqualTo("Greedy Transporter Optimizer");
    }

    @Test
    void shouldSupportValidParameters() {
        // Given
        OptimizationParameters validParams = OptimizationParameters.defaultParameters(3);
        OptimizationParameters invalidParams = null;

        // When/Then
        assertThat(optimizer.supportsParameters(validParams)).isTrue();
        assertThat(optimizer.supportsParameters(invalidParams)).isFalse();
    }

    @Test
    void shouldValidateInputsCorrectly() {
        // Given
        OptimizationParameters params = OptimizationParameters.defaultParameters(3);

        // When - Valid inputs
        ValidationResult validResult = optimizer.validate(testLanes, testQuotes, params);

        // Then
        assertThat(validResult.isValid()).isTrue();
        assertThat(validResult.getErrors()).isEmpty();
    }

    @Test
    void shouldDetectNullInputs() {
        // Given
        OptimizationParameters params = OptimizationParameters.defaultParameters(3);

        // When/Then
        ValidationResult result1 = optimizer.validate(null, testQuotes, params);
        assertThat(result1.isValid()).isFalse();
        assertThat(result1.getErrors()).contains("Lanes list cannot be null or empty");

        ValidationResult result2 = optimizer.validate(testLanes, null, params);
        assertThat(result2.isValid()).isFalse();
        assertThat(result2.getErrors()).contains("Lane quotes list cannot be null or empty");

        ValidationResult result3 = optimizer.validate(testLanes, testQuotes, null);
        assertThat(result3.isValid()).isFalse();
        assertThat(result3.getErrors()).contains("Optimization parameters cannot be null");
    }

    @Test
    void shouldDetectUncoveredLanes() {
        // Given
        Lane uncoveredLane = new Lane(4L, "Kolkata", "Guwahati");
        List<Lane> lanesWithUncovered = List.of(testLanes.get(0), uncoveredLane);
        OptimizationParameters params = OptimizationParameters.defaultParameters(3);

        // When
        ValidationResult result = optimizer.validate(lanesWithUncovered, testQuotes, params);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(error -> error.contains("No quotes available for lanes"));
    }

    @Test
    void shouldOptimizeSimpleCase() throws OptimizationException {
        // Given - Simple case where each transporter covers different lanes
        Lane lane1 = new Lane(1L, "Mumbai", "Delhi");
        Lane lane2 = new Lane(2L, "Chennai", "Bangalore");
        
        Transporter t1 = new Transporter(1L, "T1");
        Transporter t2 = new Transporter(2L, "T2");
        
        List<Lane> lanes = List.of(lane1, lane2);
        List<LaneQuote> quotes = List.of(
                new LaneQuote(t1, lane1, new BigDecimal("5000")),
                new LaneQuote(t2, lane2, new BigDecimal("7000"))
        );
        
        OptimizationParameters params = OptimizationParameters.defaultParameters(2);

        // When
        OptimizationResult result = optimizer.optimize(lanes, quotes, params);

        // Then
        assertThat(result.isFeasible()).isTrue();
        assertThat(result.getAssignments()).hasSize(2);
        assertThat(result.getTotalCost()).isEqualTo(new BigDecimal("12000"));
        assertThat(result.getSelectedTransporters()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void shouldChooseLowestCostOption() throws OptimizationException {
        // Given - Multiple transporters for same lane, should choose lowest cost
        Lane lane1 = new Lane(1L, "Mumbai", "Delhi");
        
        Transporter t1 = new Transporter(1L, "T1");
        Transporter t2 = new Transporter(2L, "T2");
        
        List<Lane> lanes = List.of(lane1);
        List<LaneQuote> quotes = List.of(
                new LaneQuote(t1, lane1, new BigDecimal("5000")),  // Cheaper
                new LaneQuote(t2, lane1, new BigDecimal("7000"))   // More expensive
        );
        
        OptimizationParameters params = OptimizationParameters.costMinimization(2);

        // When
        OptimizationResult result = optimizer.optimize(lanes, quotes, params);

        // Then
        assertThat(result.isFeasible()).isTrue();
        assertThat(result.getAssignments()).hasSize(1);
        assertThat(result.getTotalCost()).isEqualTo(new BigDecimal("5000"));
        assertThat(result.getAssignments().get(0).getTransporterId()).isEqualTo(1L);
    }

    @Test
    void shouldRespectMaxTransportersConstraint() throws OptimizationException {
        // Given
        OptimizationParameters params = OptimizationParameters.defaultParameters(2); // Max 2 transporters

        // When
        OptimizationResult result = optimizer.optimize(testLanes, testQuotes, params);

        // Then
        assertThat(result.isFeasible()).isTrue();
        assertThat(result.getSelectedTransporters()).hasSizeLessThanOrEqualTo(2);
        assertThat(result.getAssignments()).hasSize(3); // All lanes should be covered
    }

    @Test
    void shouldHandleInfeasibleCase() throws OptimizationException {
        // Given - Lane with no quotes
        Lane uncoveredLane = new Lane(4L, "Kolkata", "Guwahati");
        List<Lane> lanesWithUncovered = List.of(testLanes.get(0), uncoveredLane);
        OptimizationParameters params = OptimizationParameters.defaultParameters(3);

        // When
        OptimizationResult result = optimizer.optimize(lanesWithUncovered, testQuotes, params);

        // Then
        assertThat(result.isFeasible()).isFalse();
        assertThat(result.getMessage()).contains("Validation failed");
    }

    @Test
    void shouldOptimizeComplexCase() throws OptimizationException {
        // Given - The full test case
        OptimizationParameters params = OptimizationParameters.defaultParameters(3);

        // When
        OptimizationResult result = optimizer.optimize(testLanes, testQuotes, params);

        // Then
        assertThat(result.isFeasible()).isTrue();
        assertThat(result.getAssignments()).hasSize(3);
        assertThat(result.getSelectedTransporters()).isNotEmpty();
        
        // Verify all lanes are assigned
        List<Long> assignedLaneIds = result.getAssignments().stream()
                .map(Assignment::getLaneId)
                .toList();
        assertThat(assignedLaneIds).containsExactlyInAnyOrder(1L, 2L, 3L);
        
        // Verify total cost is calculated correctly
        BigDecimal expectedTotal = result.getAssignments().stream()
                .map(Assignment::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(result.getTotalCost()).isEqualTo(expectedTotal);
    }

    @Test
    void shouldHandleEmptyInputs() {
        // Given
        OptimizationParameters params = OptimizationParameters.defaultParameters(3);

        // When/Then
        ValidationResult result = optimizer.validate(List.of(), testQuotes, params);
        assertThat(result.isValid()).isFalse();

        ValidationResult result2 = optimizer.validate(testLanes, List.of(), params);
        assertThat(result2.isValid()).isFalse();
    }

    @Test
    void shouldDetectDuplicateLanes() {
        // Given - Duplicate lanes
        List<Lane> duplicateLanes = List.of(
                new Lane(1L, "Mumbai", "Delhi"),
                new Lane(1L, "Mumbai", "Delhi")  // Duplicate
        );
        OptimizationParameters params = OptimizationParameters.defaultParameters(3);

        // When
        ValidationResult result = optimizer.validate(duplicateLanes, testQuotes, params);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).contains("Duplicate lanes found in input");
    }

    @Test
    void shouldGenerateWarningsForInsufficientTransporters() {
        // Given - More max transporters than available
        OptimizationParameters params = OptimizationParameters.defaultParameters(10); // More than available

        // When
        ValidationResult result = optimizer.validate(testLanes, testQuotes, params);

        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.hasWarnings()).isTrue();
        assertThat(result.getWarnings()).anyMatch(warning -> 
                warning.contains("Available transporters") && warning.contains("is less than max transporters"));
    }
}
