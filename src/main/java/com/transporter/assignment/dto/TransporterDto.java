package com.transporter.assignment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object for Transporter information.
 */
public class TransporterDto {

    @NotNull(message = "Transporter ID cannot be null")
    private Long id;

    @NotBlank(message = "Transporter name cannot be blank")
    private String name;

    @NotEmpty(message = "Lane quotes cannot be empty")
    @Valid
    private List<LaneQuoteDto> laneQuotes;

    /**
     * Default constructor.
     */
    public TransporterDto() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id         the transporter ID
     * @param name       the transporter name
     * @param laneQuotes the list of lane quotes
     */
    public TransporterDto(Long id, String name, List<LaneQuoteDto> laneQuotes) {
        this.id = id;
        this.name = name;
        this.laneQuotes = laneQuotes;
    }

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

    public List<LaneQuoteDto> getLaneQuotes() {
        return laneQuotes;
    }

    public void setLaneQuotes(List<LaneQuoteDto> laneQuotes) {
        this.laneQuotes = laneQuotes;
    }

    @Override
    public String toString() {
        return "TransporterDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", laneQuotes=" + (laneQuotes != null ? laneQuotes.size() : 0) + " quotes" +
                '}';
    }
}
