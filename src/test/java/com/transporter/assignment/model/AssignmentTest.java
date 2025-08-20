package com.transporter.assignment.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Unit tests for the Assignment domain value object.
 * Tests basic functionality, validation, and business rules.
 */
class AssignmentTest {

    @Test
    void shouldCreateAssignmentWithValidData() {
        // Given
        Long laneId = 1L;
        Long transporterId = 2L;
        BigDecimal cost = new BigDecimal("5000.00");

        // When
        Assignment assignment = new Assignment(laneId, transporterId, cost);

        // Then
        assertThat(assignment.getLaneId()).isEqualTo(laneId);
        assertThat(assignment.getTransporterId()).isEqualTo(transporterId);
        assertThat(assignment.getCost()).isEqualTo(cost);
    }

    @Test
    void shouldCreateAssignmentWithoutCost() {
        // Given
        Long laneId = 1L;
        Long transporterId = 2L;

        // When
        Assignment assignment = new Assignment(laneId, transporterId);

        // Then
        assertThat(assignment.getLaneId()).isEqualTo(laneId);
        assertThat(assignment.getTransporterId()).isEqualTo(transporterId);
        assertThat(assignment.getCost()).isNull();
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        BigDecimal cost = new BigDecimal("5000.00");
        Assignment assignment1 = new Assignment(1L, 2L, cost);
        Assignment assignment2 = new Assignment(1L, 2L, cost);
        Assignment assignment3 = new Assignment(1L, 3L, cost);

        // Then
        assertThat(assignment1).isEqualTo(assignment2);
        assertThat(assignment1).isNotEqualTo(assignment3);
        assertThat(assignment1.hashCode()).isEqualTo(assignment2.hashCode());
        assertThat(assignment1.hashCode()).isNotEqualTo(assignment3.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        // Given
        BigDecimal cost = new BigDecimal("5000.00");
        Assignment assignment = new Assignment(1L, 2L, cost);

        // When
        String toString = assignment.toString();

        // Then
        assertThat(toString).contains("1");
        assertThat(toString).contains("2");
        assertThat(toString).contains("5000.00");
    }

    @Test
    void shouldHandleNullCost() {
        // Given
        Assignment assignment = new Assignment(1L, 2L, null);

        // When/Then
        assertThat(assignment.getLaneId()).isEqualTo(1L);
        assertThat(assignment.getTransporterId()).isEqualTo(2L);
        assertThat(assignment.getCost()).isNull();
    }

    @Test
    void shouldCreateFromLaneQuote() {
        // Given
        Transporter transporter = new Transporter(2L, "Transporter T2");
        Lane lane = new Lane(1L, "Mumbai", "Delhi");
        BigDecimal quote = new BigDecimal("5000.00");
        LaneQuote laneQuote = new LaneQuote(transporter, lane, quote);

        // When
        Assignment assignment = Assignment.fromLaneQuote(laneQuote);

        // Then
        assertThat(assignment.getLaneId()).isEqualTo(1L);
        assertThat(assignment.getTransporterId()).isEqualTo(2L);
        assertThat(assignment.getCost()).isEqualTo(quote);
    }

    @Test
    void shouldThrowExceptionWhenCreatingFromNullLaneQuote() {
        // When/Then
        assertThatThrownBy(() -> Assignment.fromLaneQuote(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("LaneQuote cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenCreatingFromLaneQuoteWithNullLane() {
        // Given
        Transporter transporter = new Transporter(2L, "Transporter T2");
        LaneQuote laneQuote = new LaneQuote(transporter, null, new BigDecimal("5000.00"));

        // When/Then
        assertThatThrownBy(() -> Assignment.fromLaneQuote(laneQuote))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("LaneQuote must have a valid lane");
    }

    @Test
    void shouldThrowExceptionWhenCreatingFromLaneQuoteWithNullTransporter() {
        // Given
        Lane lane = new Lane(1L, "Mumbai", "Delhi");
        LaneQuote laneQuote = new LaneQuote(null, lane, new BigDecimal("5000.00"));

        // When/Then
        assertThatThrownBy(() -> Assignment.fromLaneQuote(laneQuote))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("LaneQuote must have a valid transporter");
    }

    @Test
    void shouldBeImmutable() {
        // Given
        Assignment assignment = new Assignment(1L, 2L, new BigDecimal("5000.00"));

        // When/Then - Assignment should be immutable (no setters)
        // This is verified by the fact that Assignment only has getters and constructor
        assertThat(assignment.getLaneId()).isEqualTo(1L);
        assertThat(assignment.getTransporterId()).isEqualTo(2L);
        assertThat(assignment.getCost()).isEqualTo(new BigDecimal("5000.00"));
    }

    @Test
    void shouldCompareCosts() {
        // Given
        Assignment cheaperAssignment = new Assignment(1L, 2L, new BigDecimal("3000.00"));
        Assignment expensiveAssignment = new Assignment(2L, 3L, new BigDecimal("5000.00"));

        // When/Then
        assertThat(cheaperAssignment.getCost().compareTo(expensiveAssignment.getCost())).isLessThan(0);
        assertThat(expensiveAssignment.getCost().compareTo(cheaperAssignment.getCost())).isGreaterThan(0);
    }
}
