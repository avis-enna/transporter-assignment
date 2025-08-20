package com.transporter.assignment.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Transporter entity representing a logistics company that can handle shipments.
 * Each transporter can provide quotes for multiple lanes.
 */
@Entity
@Table(name = "transporters", indexes = {
    @Index(name = "idx_transporter_name", columnList = "name"),
    @Index(name = "idx_transporter_capacity", columnList = "capacity")
})
public class Transporter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "transporter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LaneQuote> laneQuotes = new ArrayList<>();
    
    // Constructors
    public Transporter() {}

    public Transporter(Long id, String name) {
        this.id = id;
        this.name = name;
        this.capacity = 100; // Default capacity
    }

    public Transporter(String name) {
        this.name = name;
        this.capacity = 100; // Default capacity
    }

    public Transporter(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<LaneQuote> getLaneQuotes() {
        return laneQuotes;
    }

    public void setLaneQuotes(List<LaneQuote> laneQuotes) {
        this.laneQuotes = laneQuotes;
    }

    /**
     * Adds a lane quote to this transporter.
     */
    public void addLaneQuote(LaneQuote laneQuote) {
        if (laneQuotes == null) {
            laneQuotes = new ArrayList<>();
        }
        laneQuotes.add(laneQuote);
        laneQuote.setTransporter(this);
    }

    /**
     * Removes a lane quote from this transporter.
     */
    public void removeLaneQuote(LaneQuote laneQuote) {
        if (laneQuotes != null) {
            laneQuotes.remove(laneQuote);
            laneQuote.setTransporter(null);
        }
    }

    /**
     * Gets the quote for a specific lane.
     */
    public LaneQuote getQuoteForLane(Lane lane) {
        if (laneQuotes == null || lane == null) {
            return null;
        }
        return laneQuotes.stream()
                .filter(quote -> Objects.equals(quote.getLane().getId(), lane.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if this transporter can service a specific lane.
     */
    public boolean canServiceLane(Lane lane) {
        return getQuoteForLane(lane) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transporter that = (Transporter) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transporter{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", capacity=" + capacity +
               ", laneQuotesCount=" + (laneQuotes != null ? laneQuotes.size() : 0) +
               '}';
    }
}
