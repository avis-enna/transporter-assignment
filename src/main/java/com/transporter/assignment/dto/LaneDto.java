package com.transporter.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Lane information.
 */
public class LaneDto {

    @NotNull(message = "Lane ID cannot be null")
    private Long id;

    @NotBlank(message = "Origin cannot be blank")
    private String origin;

    @NotBlank(message = "Destination cannot be blank")
    private String destination;

    /**
     * Default constructor.
     */
    public LaneDto() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id          the lane ID
     * @param origin      the origin city
     * @param destination the destination city
     */
    public LaneDto(Long id, String origin, String destination) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "LaneDto{" +
                "id=" + id +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
