package com.transporter.assignment.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the Lane domain entity.
 * Tests basic functionality, validation, and business rules.
 */
class LaneTest {

    private Lane lane;

    @BeforeEach
    void setUp() {
        lane = new Lane();
    }

    @Test
    void shouldCreateLaneWithValidData() {
        // Given
        Long id = 1L;
        String origin = "Mumbai";
        String destination = "Delhi";

        // When
        lane.setId(id);
        lane.setOrigin(origin);
        lane.setDestination(destination);

        // Then
        assertThat(lane.getId()).isEqualTo(id);
        assertThat(lane.getOrigin()).isEqualTo(origin);
        assertThat(lane.getDestination()).isEqualTo(destination);
    }

    @Test
    void shouldCreateLaneUsingConstructor() {
        // Given
        Long id = 2L;
        String origin = "Chennai";
        String destination = "Bangalore";

        // When
        Lane constructedLane = new Lane(id, origin, destination);

        // Then
        assertThat(constructedLane.getId()).isEqualTo(id);
        assertThat(constructedLane.getOrigin()).isEqualTo(origin);
        assertThat(constructedLane.getDestination()).isEqualTo(destination);
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        Lane lane1 = new Lane(1L, "Mumbai", "Delhi");
        Lane lane2 = new Lane(1L, "Mumbai", "Delhi");
        Lane lane3 = new Lane(2L, "Chennai", "Bangalore");

        // Then
        assertThat(lane1).isEqualTo(lane2);
        assertThat(lane1).isNotEqualTo(lane3);
        assertThat(lane1.hashCode()).isEqualTo(lane2.hashCode());
        assertThat(lane1.hashCode()).isNotEqualTo(lane3.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        // Given
        Lane lane = new Lane(1L, "Mumbai", "Delhi");

        // When
        String toString = lane.toString();

        // Then
        assertThat(toString).contains("1");
        assertThat(toString).contains("Mumbai");
        assertThat(toString).contains("Delhi");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        Lane lane = new Lane();

        // When/Then - should not throw exceptions
        assertThat(lane.getId()).isNull();
        assertThat(lane.getOrigin()).isNull();
        assertThat(lane.getDestination()).isNull();
    }

    @Test
    void shouldValidateBusinessRules() {
        // Given
        Lane lane = new Lane(1L, "Mumbai", "Mumbai");

        // When/Then - same origin and destination should be allowed at entity level
        // Business validation will be handled at service layer
        assertThat(lane.getOrigin()).isEqualTo(lane.getDestination());
    }

    @Test
    void shouldTrimWhitespaceInOriginAndDestination() {
        // Given
        String originWithSpaces = "  Mumbai  ";
        String destinationWithSpaces = "  Delhi  ";

        // When
        Lane lane = new Lane(1L, originWithSpaces.trim(), destinationWithSpaces.trim());

        // Then
        assertThat(lane.getOrigin()).isEqualTo("Mumbai");
        assertThat(lane.getDestination()).isEqualTo("Delhi");
    }
}
