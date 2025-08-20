package com.transporter.assignment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain entity representing a quote from a transporter for a specific lane.
 * This entity links a transporter to a lane with their quoted price.
 */
@Entity
@Table(name = "lane_quote", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"transporter_id", "lane_id"}))
public class LaneQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    @NotNull(message = "Transporter cannot be null")
    private Transporter transporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lane_id", nullable = false)
    @NotNull(message = "Lane cannot be null")
    private Lane lane;

    @Column(name = "quote", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Quote cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Quote must be non-negative")
    private BigDecimal quote;

    /**
     * Default constructor for JPA.
     */
    public LaneQuote() {
    }

    /**
     * Constructor with all required fields.
     *
     * @param transporter the transporter providing the quote
     * @param lane        the lane for which the quote is provided
     * @param quote       the quoted price
     */
    public LaneQuote(Transporter transporter, Lane lane, BigDecimal quote) {
        this.transporter = transporter;
        this.lane = lane;
        this.quote = quote;
    }

    /**
     * Gets the quote ID.
     *
     * @return the quote ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the quote ID.
     *
     * @param id the quote ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the transporter.
     *
     * @return the transporter
     */
    public Transporter getTransporter() {
        return transporter;
    }

    /**
     * Sets the transporter.
     *
     * @param transporter the transporter
     */
    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    /**
     * Gets the lane.
     *
     * @return the lane
     */
    public Lane getLane() {
        return lane;
    }

    /**
     * Sets the lane.
     *
     * @param lane the lane
     */
    public void setLane(Lane lane) {
        this.lane = lane;
    }

    /**
     * Gets the quote amount.
     *
     * @return the quote amount
     */
    public BigDecimal getQuote() {
        return quote;
    }

    /**
     * Sets the quote amount.
     *
     * @param quote the quote amount
     */
    public void setQuote(BigDecimal quote) {
        this.quote = quote;
    }

    /**
     * Gets the lane ID.
     *
     * @return the lane ID, or null if lane is null
     */
    public Long getLaneId() {
        return lane != null ? lane.getId() : null;
    }

    /**
     * Gets the transporter ID.
     *
     * @return the transporter ID, or null if transporter is null
     */
    public Long getTransporterId() {
        return transporter != null ? transporter.getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaneQuote laneQuote = (LaneQuote) o;
        return Objects.equals(transporter, laneQuote.transporter) &&
                Objects.equals(lane, laneQuote.lane) &&
                Objects.equals(quote, laneQuote.quote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transporter, lane, quote);
    }

    @Override
    public String toString() {
        return "LaneQuote{" +
                "id=" + id +
                ", transporter=" + (transporter != null ? transporter.getName() : "null") +
                ", lane=" + (lane != null ? lane.getOrigin() + "->" + lane.getDestination() : "null") +
                ", quote=" + quote +
                '}';
    }
}
