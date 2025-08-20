package com.transporter.assignment.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Assignment information.
 */
public class AssignmentDto {

    private Long laneId;
    private Long transporterId;
    private BigDecimal cost;

    /**
     * Default constructor.
     */
    public AssignmentDto() {
    }

    /**
     * Constructor with all fields.
     *
     * @param laneId        the lane ID
     * @param transporterId the transporter ID
     * @param cost          the assignment cost
     */
    public AssignmentDto(Long laneId, Long transporterId, BigDecimal cost) {
        this.laneId = laneId;
        this.transporterId = transporterId;
        this.cost = cost;
    }

    public Long getLaneId() {
        return laneId;
    }

    public void setLaneId(Long laneId) {
        this.laneId = laneId;
    }

    public Long getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(Long transporterId) {
        this.transporterId = transporterId;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "AssignmentDto{" +
                "laneId=" + laneId +
                ", transporterId=" + transporterId +
                ", cost=" + cost +
                '}';
    }
}
