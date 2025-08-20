package com.transporter.assignment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for submitting input data (lanes and transporters).
 */
public class InputDataRequest {

    @NotEmpty(message = "Lanes list cannot be empty")
    @Valid
    private List<LaneDto> lanes;

    @NotEmpty(message = "Transporters list cannot be empty")
    @Valid
    private List<TransporterDto> transporters;

    /**
     * Default constructor.
     */
    public InputDataRequest() {
    }

    /**
     * Constructor with all fields.
     *
     * @param lanes        the list of lanes
     * @param transporters the list of transporters
     */
    public InputDataRequest(List<LaneDto> lanes, List<TransporterDto> transporters) {
        this.lanes = lanes;
        this.transporters = transporters;
    }

    public List<LaneDto> getLanes() {
        return lanes;
    }

    public void setLanes(List<LaneDto> lanes) {
        this.lanes = lanes;
    }

    public List<TransporterDto> getTransporters() {
        return transporters;
    }

    public void setTransporters(List<TransporterDto> transporters) {
        this.transporters = transporters;
    }

    @Override
    public String toString() {
        return "InputDataRequest{" +
                "lanes=" + (lanes != null ? lanes.size() : 0) + " lanes" +
                ", transporters=" + (transporters != null ? transporters.size() : 0) + " transporters" +
                '}';
    }
}
