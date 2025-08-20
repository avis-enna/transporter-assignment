package com.transporter.assignment.algorithm;

import com.transporter.assignment.model.Assignment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for OptimizationResult.
 */
class OptimizationResultTest {

    @Test
    void shouldCreateSuccessfulResult() {
        // Given
        List<Assignment> assignments = List.of(
                new Assignment(1L, 1L, new BigDecimal("5000")),
                new Assignment(2L, 2L, new BigDecimal("7000"))
        );
        BigDecimal totalCost = new BigDecimal("12000");
        List<Long> selectedTransporters = List.of(1L, 2L);

        // When
        OptimizationResult result = new OptimizationResult(assignments, totalCost, selectedTransporters);

        // Then
        assertThat(result.getAssignments()).hasSize(2);
        assertThat(result.getTotalCost()).isEqualTo(totalCost);
        assertThat(result.getSelectedTransporters()).containsExactly(1L, 2L);
        assertThat(result.isFeasible()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Optimization completed successfully");
        assertThat(result.getTransporterCount()).isEqualTo(2);
        assertThat(result.getLaneCount()).isEqualTo(2);
    }

    @Test
    void shouldCreateResultWithCustomMessage() {
        // Given
        List<Assignment> assignments = List.of(new Assignment(1L, 1L, new BigDecimal("5000")));
        BigDecimal totalCost = new BigDecimal("5000");
        List<Long> selectedTransporters = List.of(1L);
        String customMessage = "Optimization completed with suboptimal solution";

        // When
        OptimizationResult result = new OptimizationResult(assignments, totalCost, selectedTransporters, true, customMessage);

        // Then
        assertThat(result.isFeasible()).isTrue();
        assertThat(result.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void shouldCreateInfeasibleResult() {
        // Given
        String reason = "No transporters available for lane 5";

        // When
        OptimizationResult result = OptimizationResult.infeasible(reason);

        // Then
        assertThat(result.isFeasible()).isFalse();
        assertThat(result.getMessage()).isEqualTo(reason);
        assertThat(result.getAssignments()).isEmpty();
        assertThat(result.getSelectedTransporters()).isEmpty();
        assertThat(result.getTotalCost()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getTransporterCount()).isEqualTo(0);
        assertThat(result.getLaneCount()).isEqualTo(0);
    }

    @Test
    void shouldHandleNullInputs() {
        // When
        OptimizationResult result = new OptimizationResult(null, null, null);

        // Then
        assertThat(result.getAssignments()).isEmpty();
        assertThat(result.getSelectedTransporters()).isEmpty();
        assertThat(result.getTotalCost()).isNull();
        assertThat(result.isFeasible()).isTrue();
    }

    @Test
    void shouldCreateImmutableCollections() {
        // Given
        List<Assignment> assignments = List.of(new Assignment(1L, 1L, new BigDecimal("5000")));
        List<Long> transporters = List.of(1L);

        // When
        OptimizationResult result = new OptimizationResult(assignments, new BigDecimal("5000"), transporters);

        // Then
        assertThatThrownBy(() -> result.getAssignments().add(new Assignment(2L, 2L)))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> result.getSelectedTransporters().add(2L))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        List<Assignment> assignments = List.of(new Assignment(1L, 1L, new BigDecimal("5000")));
        BigDecimal totalCost = new BigDecimal("5000");
        List<Long> transporters = List.of(1L);

        OptimizationResult result1 = new OptimizationResult(assignments, totalCost, transporters);
        OptimizationResult result2 = new OptimizationResult(assignments, totalCost, transporters);
        OptimizationResult result3 = new OptimizationResult(assignments, new BigDecimal("6000"), transporters);

        // Then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        assertThat(result1.hashCode()).isNotEqualTo(result3.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        // Given
        List<Assignment> assignments = List.of(
                new Assignment(1L, 1L, new BigDecimal("5000")),
                new Assignment(2L, 2L, new BigDecimal("7000"))
        );
        OptimizationResult result = new OptimizationResult(assignments, new BigDecimal("12000"), List.of(1L, 2L));

        // When
        String toString = result.toString();

        // Then
        assertThat(toString).contains("2 lanes");
        assertThat(toString).contains("2 transporters");
        assertThat(toString).contains("12000");
        assertThat(toString).contains("feasible=true");
    }

    @Test
    void shouldHandleEmptyAssignments() {
        // When
        OptimizationResult result = new OptimizationResult(List.of(), BigDecimal.ZERO, List.of());

        // Then
        assertThat(result.getAssignments()).isEmpty();
        assertThat(result.getSelectedTransporters()).isEmpty();
        assertThat(result.getTotalCost()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getTransporterCount()).isEqualTo(0);
        assertThat(result.getLaneCount()).isEqualTo(0);
        assertThat(result.isFeasible()).isTrue();
    }
}
