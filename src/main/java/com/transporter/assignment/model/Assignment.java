package com.transporter.assignment.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain value object representing an assignment of a transporter to a lane.
 * This is an immutable object that represents the result of the optimization algorithm.
 */
public class Assignment {

    private final Long laneId;
    private final Long transporterId;
    private final BigDecimal cost;

    /**
     * Constructor with lane ID and transporter ID.
     *
     * @param laneId        the ID of the assigned lane
     * @param transporterId the ID of the assigned transporter
     */
    public Assignment(Long laneId, Long transporterId) {
        this(laneId, transporterId, null);
    }

    /**
     * Constructor with all fields.
     *
     * @param laneId        the ID of the assigned lane
     * @param transporterId the ID of the assigned transporter
     * @param cost          the cost of the assignment
     */
    public Assignment(Long laneId, Long transporterId, BigDecimal cost) {
        this.laneId = laneId;
        this.transporterId = transporterId;
        this.cost = cost;
    }

    /**
     * Creates an Assignment from a LaneQuote.
     *
     * @param laneQuote the lane quote to create assignment from
     * @return the assignment
     * @throws IllegalArgumentException if laneQuote is null or invalid
     */
    public static Assignment fromLaneQuote(LaneQuote laneQuote) {
        if (laneQuote == null) {
            throw new IllegalArgumentException("LaneQuote cannot be null");
        }
        if (laneQuote.getLane() == null) {
            throw new IllegalArgumentException("LaneQuote must have a valid lane");
        }
        if (laneQuote.getTransporter() == null) {
            throw new IllegalArgumentException("LaneQuote must have a valid transporter");
        }

        return new Assignment(
                laneQuote.getLane().getId(),
                laneQuote.getTransporter().getId(),
                laneQuote.getQuote()
        );
    }

    /**
     * Gets the lane ID.
     *
     * @return the lane ID
     */
    public Long getLaneId() {
        return laneId;
    }

    /**
     * Gets the transporter ID.
     *
     * @return the transporter ID
     */
    public Long getTransporterId() {
        return transporterId;
    }

    /**
     * Gets the cost of the assignment.
     *
     * @return the cost, or null if not specified
     */
    public BigDecimal getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(laneId, that.laneId) &&
                Objects.equals(transporterId, that.transporterId) &&
                Objects.equals(cost, that.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(laneId, transporterId, cost);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "laneId=" + laneId +
                ", transporterId=" + transporterId +
                ", cost=" + cost +
                '}';
    }
}
