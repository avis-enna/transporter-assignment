package com.transporter.assignment.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Unit tests for the LaneQuote domain entity.
 * Tests basic functionality, validation, and business rules.
 */
class LaneQuoteTest {

    private LaneQuote laneQuote;
    private Transporter transporter;
    private Lane lane;

    @BeforeEach
    void setUp() {
        transporter = new Transporter(1L, "Transporter T1");
        lane = new Lane(1L, "Mumbai", "Delhi");
        laneQuote = new LaneQuote();
    }

    @Test
    void shouldCreateLaneQuoteWithValidData() {
        // Given
        BigDecimal quote = new BigDecimal("5000.00");

        // When
        laneQuote.setTransporter(transporter);
        laneQuote.setLane(lane);
        laneQuote.setQuote(quote);

        // Then
        assertThat(laneQuote.getTransporter()).isEqualTo(transporter);
        assertThat(laneQuote.getLane()).isEqualTo(lane);
        assertThat(laneQuote.getQuote()).isEqualTo(quote);
    }

    @Test
    void shouldCreateLaneQuoteUsingConstructor() {
        // Given
        BigDecimal quote = new BigDecimal("7500.50");

        // When
        LaneQuote constructedQuote = new LaneQuote(transporter, lane, quote);

        // Then
        assertThat(constructedQuote.getTransporter()).isEqualTo(transporter);
        assertThat(constructedQuote.getLane()).isEqualTo(lane);
        assertThat(constructedQuote.getQuote()).isEqualTo(quote);
    }

    @Test
    void shouldHandleDecimalQuotes() {
        // Given
        BigDecimal quote = new BigDecimal("5000.75");

        // When
        LaneQuote laneQuote = new LaneQuote(transporter, lane, quote);

        // Then
        assertThat(laneQuote.getQuote()).isEqualTo(quote);
        assertThat(laneQuote.getQuote().scale()).isEqualTo(2);
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        BigDecimal quote = new BigDecimal("5000.00");
        LaneQuote quote1 = new LaneQuote(transporter, lane, quote);
        LaneQuote quote2 = new LaneQuote(transporter, lane, quote);
        
        Transporter differentTransporter = new Transporter(2L, "Transporter T2");
        LaneQuote quote3 = new LaneQuote(differentTransporter, lane, quote);

        // Then
        assertThat(quote1).isEqualTo(quote2);
        assertThat(quote1).isNotEqualTo(quote3);
        assertThat(quote1.hashCode()).isEqualTo(quote2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        // Given
        BigDecimal quote = new BigDecimal("5000.00");
        LaneQuote laneQuote = new LaneQuote(transporter, lane, quote);

        // When
        String toString = laneQuote.toString();

        // Then
        assertThat(toString).contains("5000.00");
        assertThat(toString).contains("Transporter T1");
        assertThat(toString).contains("Mumbai");
        assertThat(toString).contains("Delhi");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        LaneQuote laneQuote = new LaneQuote();

        // When/Then - should not throw exceptions
        assertThat(laneQuote.getTransporter()).isNull();
        assertThat(laneQuote.getLane()).isNull();
        assertThat(laneQuote.getQuote()).isNull();
    }

    @Test
    void shouldValidatePositiveQuote() {
        // Given
        BigDecimal positiveQuote = new BigDecimal("1000.00");
        BigDecimal zeroQuote = BigDecimal.ZERO;
        BigDecimal negativeQuote = new BigDecimal("-500.00");

        // When/Then - entity level allows any value, validation at service layer
        LaneQuote positiveQuoteEntity = new LaneQuote(transporter, lane, positiveQuote);
        LaneQuote zeroQuoteEntity = new LaneQuote(transporter, lane, zeroQuote);
        LaneQuote negativeQuoteEntity = new LaneQuote(transporter, lane, negativeQuote);

        assertThat(positiveQuoteEntity.getQuote()).isEqualTo(positiveQuote);
        assertThat(zeroQuoteEntity.getQuote()).isEqualTo(zeroQuote);
        assertThat(negativeQuoteEntity.getQuote()).isEqualTo(negativeQuote);
    }

    @Test
    void shouldCompareQuotes() {
        // Given
        BigDecimal lowerQuote = new BigDecimal("3000.00");
        BigDecimal higherQuote = new BigDecimal("5000.00");
        
        LaneQuote lowerQuoteEntity = new LaneQuote(transporter, lane, lowerQuote);
        LaneQuote higherQuoteEntity = new LaneQuote(transporter, lane, higherQuote);

        // When/Then
        assertThat(lowerQuoteEntity.getQuote().compareTo(higherQuoteEntity.getQuote())).isLessThan(0);
        assertThat(higherQuoteEntity.getQuote().compareTo(lowerQuoteEntity.getQuote())).isGreaterThan(0);
    }

    @Test
    void shouldGetLaneId() {
        // Given
        BigDecimal quote = new BigDecimal("5000.00");
        LaneQuote laneQuote = new LaneQuote(transporter, lane, quote);

        // When
        Long laneId = laneQuote.getLaneId();

        // Then
        assertThat(laneId).isEqualTo(1L);
    }

    @Test
    void shouldGetTransporterId() {
        // Given
        BigDecimal quote = new BigDecimal("5000.00");
        LaneQuote laneQuote = new LaneQuote(transporter, lane, quote);

        // When
        Long transporterId = laneQuote.getTransporterId();

        // Then
        assertThat(transporterId).isEqualTo(1L);
    }
}
