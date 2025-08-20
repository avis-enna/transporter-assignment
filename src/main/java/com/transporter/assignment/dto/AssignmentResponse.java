package com.transporter.assignment.dto;

import com.transporter.assignment.model.Assignment;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for assignment optimization results.
 */
public class AssignmentResponse {

    private String status;
    private String message;
    private BigDecimal totalCost;
    private List<AssignmentDto> assignments;
    private List<Long> selectedTransporters;

    /**
     * Default constructor.
     */
    public AssignmentResponse() {
    }

    /**
     * Constructor for successful response.
     *
     * @param totalCost            the total cost
     * @param assignments          the list of assignments
     * @param selectedTransporters the selected transporters
     */
    public AssignmentResponse(BigDecimal totalCost, List<AssignmentDto> assignments, List<Long> selectedTransporters) {
        this.status = "success";
        this.message = "Assignment optimization completed successfully";
        this.totalCost = totalCost;
        this.assignments = assignments;
        this.selectedTransporters = selectedTransporters;
    }

    /**
     * Constructor for error response.
     *
     * @param status  the status
     * @param message the error message
     */
    public AssignmentResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.totalCost = BigDecimal.ZERO;
        this.assignments = List.of();
        this.selectedTransporters = List.of();
    }

    /**
     * Creates a successful response.
     *
     * @param totalCost            the total cost
     * @param assignments          the assignments
     * @param selectedTransporters the selected transporters
     * @return successful response
     */
    public static AssignmentResponse success(BigDecimal totalCost, List<AssignmentDto> assignments, List<Long> selectedTransporters) {
        return new AssignmentResponse(totalCost, assignments, selectedTransporters);
    }

    /**
     * Creates an error response.
     *
     * @param message the error message
     * @return error response
     */
    public static AssignmentResponse error(String message) {
        return new AssignmentResponse("error", message);
    }

    /**
     * Creates a failure response.
     *
     * @param message the failure message
     * @return failure response
     */
    public static AssignmentResponse failure(String message) {
        return new AssignmentResponse("failure", message);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public List<AssignmentDto> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentDto> assignments) {
        this.assignments = assignments;
    }

    public List<Long> getSelectedTransporters() {
        return selectedTransporters;
    }

    public void setSelectedTransporters(List<Long> selectedTransporters) {
        this.selectedTransporters = selectedTransporters;
    }

    /**
     * Creates a copy with a different message.
     *
     * @param newMessage the new message
     * @return response with updated message
     */
    public AssignmentResponse withMessage(String newMessage) {
        AssignmentResponse response = new AssignmentResponse();
        response.status = this.status;
        response.message = newMessage;
        response.totalCost = this.totalCost;
        response.assignments = this.assignments;
        response.selectedTransporters = this.selectedTransporters;
        return response;
    }

    @Override
    public String toString() {
        return "AssignmentResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", totalCost=" + totalCost +
                ", assignments=" + (assignments != null ? assignments.size() : 0) + " assignments" +
                ", selectedTransporters=" + (selectedTransporters != null ? selectedTransporters.size() : 0) + " transporters" +
                '}';
    }
}
