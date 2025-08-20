package com.transporter.assignment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a transportation lane (route) between two cities.
 */
@Entity
@Table(name = "lane")
public class Lane {

    @Id
    @NotNull(message = "Lane ID cannot be null")
    private Long id;

    @Column(name = "origin", nullable = false)
    @NotBlank(message = "Origin cannot be blank")
    private String origin;

    @Column(name = "destination", nullable = false)
    @NotBlank(message = "Destination cannot be blank")
    private String destination;

    @OneToMany(mappedBy = "lane", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LaneQuote> laneQuotes = new ArrayList<>();

    // Default constructor for JPA
    public Lane() {
    }

    public Lane(Long id, String origin, String destination) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
    }

    // Getters and setters
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

    public List<LaneQuote> getLaneQuotes() {
        return laneQuotes;
    }

    public void setLaneQuotes(List<LaneQuote> laneQuotes) {
        this.laneQuotes = laneQuotes != null ? laneQuotes : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lane lane = (Lane) o;
        return Objects.equals(id, lane.id) &&
                Objects.equals(origin, lane.origin) &&
                Objects.equals(destination, lane.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, origin, destination);
    }

    @Override
    public String toString() {
        return "Lane{" +
                "id=" + id +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
