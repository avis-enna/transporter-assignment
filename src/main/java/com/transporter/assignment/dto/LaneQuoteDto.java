package com.transporter.assignment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Data Transfer Object for LaneQuote information.
 */
public class LaneQuoteDto {

    @NotNull(message = "Lane ID cannot be null")
    private Long laneId;

    @NotNull(message = "Quote cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Quote must be non-negative")
    private BigDecimal quote;

    /**
     * Default constructor.
     */
    public LaneQuoteDto() {
    }

    /**
     * Constructor with all fields.
     *
     * @param laneId the lane ID
     * @param quote  the quote amount
     */
    public LaneQuoteDto(Long laneId, BigDecimal quote) {
        this.laneId = laneId;
        this.quote = quote;
    }

    public Long getLaneId() {
        return laneId;
    }

    public void setLaneId(Long laneId) {
        this.laneId = laneId;
    }

    public BigDecimal getQuote() {
        return quote;
    }

    public void setQuote(BigDecimal quote) {
        this.quote = quote;
    }

    @Override
    public String toString() {
        return "LaneQuoteDto{" +
                "laneId=" + laneId +
                ", quote=" + quote +
                '}';
    }
}
