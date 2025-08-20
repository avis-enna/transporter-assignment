package com.transporter.assignment.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for Transporter
 */
class TransporterTest {

    private Transporter transporter;

    @BeforeEach
    void setUp() {
        transporter = new Transporter();
    }

    @Test
    void shouldCreateTransporter() {
        Long id = 1L;
        String name = "Transporter T1";
        Integer capacity = 5;

        transporter.setId(id);
        transporter.setName(name);
        transporter.setCapacity(capacity);

        assertThat(transporter.getId()).isEqualTo(id);
        assertThat(transporter.getName()).isEqualTo(name);
        assertThat(transporter.getCapacity()).isEqualTo(capacity);
    }

    @Test
    void testConstructorWithParams() {
        Transporter t = new Transporter("Test", 10);
        assertThat(t.getName()).isEqualTo("Test");
        assertThat(t.getCapacity()).isEqualTo(10);
    }

    @Test
    void testToString() {
        transporter.setId(1L);
        transporter.setName("Test");
        transporter.setCapacity(5);

        String str = transporter.toString();
        assertThat(str).contains("Test");
        assertThat(str).contains("5");
    }

    @Test
    void shouldCreateTransporterUsingConstructor() {
        // Given
        Long id = 2L;
        String name = "Transporter T2";

        // When
        Transporter constructedTransporter = new Transporter(id, name);

        // Then
        assertThat(constructedTransporter.getId()).isEqualTo(id);
        assertThat(constructedTransporter.getName()).isEqualTo(name);
        assertThat(constructedTransporter.getLaneQuotes()).isNotNull();
        assertThat(constructedTransporter.getLaneQuotes()).isEmpty();
    }

    @Test
    void shouldManageLaneQuotes() {
        // Given
        Transporter transporter = new Transporter(1L, "Transporter T1");
        Lane lane1 = new Lane(1L, "Mumbai", "Delhi");
        Lane lane2 = new Lane(2L, "Chennai", "Bangalore");
        
        LaneQuote quote1 = new LaneQuote(transporter, lane1, new BigDecimal("5000"));
        LaneQuote quote2 = new LaneQuote(transporter, lane2, new BigDecimal("7000"));

        // When
        transporter.addLaneQuote(quote1);
        transporter.addLaneQuote(quote2);

        // Then
        assertThat(transporter.getLaneQuotes()).hasSize(2);
        assertThat(transporter.getLaneQuotes()).contains(quote1, quote2);
    }

    @Test
    void shouldRemoveLaneQuotes() {
        // Given
        Transporter transporter = new Transporter(1L, "Transporter T1");
        Lane lane = new Lane(1L, "Mumbai", "Delhi");
        LaneQuote quote = new LaneQuote(transporter, lane, new BigDecimal("5000"));
        
        transporter.addLaneQuote(quote);

        // When
        transporter.removeLaneQuote(quote);

        // Then
        assertThat(transporter.getLaneQuotes()).isEmpty();
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        Transporter transporter1 = new Transporter(1L, "Transporter T1");
        Transporter transporter2 = new Transporter(1L, "Transporter T1");
        Transporter transporter3 = new Transporter(2L, "Transporter T2");

        // Then
        assertThat(transporter1).isEqualTo(transporter2);
        assertThat(transporter1).isNotEqualTo(transporter3);
        assertThat(transporter1.hashCode()).isEqualTo(transporter2.hashCode());
        assertThat(transporter1.hashCode()).isNotEqualTo(transporter3.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        // Given
        Transporter transporter = new Transporter(1L, "Transporter T1");

        // When
        String toString = transporter.toString();

        // Then
        assertThat(toString).contains("1");
        assertThat(toString).contains("Transporter T1");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        Transporter transporter = new Transporter();

        // When/Then - should not throw exceptions
        assertThat(transporter.getId()).isNull();
        assertThat(transporter.getName()).isNull();
        assertThat(transporter.getLaneQuotes()).isNotNull();
    }

    @Test
    void shouldFindQuoteForLane() {
        // Given
        Transporter transporter = new Transporter(1L, "Transporter T1");
        Lane lane1 = new Lane(1L, "Mumbai", "Delhi");
        Lane lane2 = new Lane(2L, "Chennai", "Bangalore");
        
        LaneQuote quote1 = new LaneQuote(transporter, lane1, new BigDecimal("5000"));
        transporter.addLaneQuote(quote1);

        // When
        LaneQuote foundQuote = transporter.getQuoteForLane(lane1);
        LaneQuote notFoundQuote = transporter.getQuoteForLane(lane2);

        // Then
        assertThat(foundQuote).isEqualTo(quote1);
        assertThat(notFoundQuote).isNull();
    }

    @Test
    void shouldCheckIfCanServiceLane() {
        // Given
        Transporter transporter = new Transporter(1L, "Transporter T1");
        Lane lane1 = new Lane(1L, "Mumbai", "Delhi");
        Lane lane2 = new Lane(2L, "Chennai", "Bangalore");
        
        LaneQuote quote1 = new LaneQuote(transporter, lane1, new BigDecimal("5000"));
        transporter.addLaneQuote(quote1);

        // When/Then
        assertThat(transporter.canServiceLane(lane1)).isTrue();
        assertThat(transporter.canServiceLane(lane2)).isFalse();
    }
}
