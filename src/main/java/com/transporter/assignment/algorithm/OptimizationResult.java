package com.transporter.assignment.algorithm;

import com.transporter.assignment.model.Assignment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Result of the transporter assignment optimization algorithm.
 * Contains the optimized assignments, total cost, and selected transporters.
 */
public class OptimizationResult {

    private final List<Assignment> assignments;
    private final BigDecimal totalCost;
    private final List<Long> selectedTransporters;
    private final boolean feasible;
    private final String message;

    /**
     * Constructor for successful optimization result.
     *
     * @param assignments          the list of lane assignments
     * @param totalCost           the total cost of all assignments
     * @param selectedTransporters the list of selected transporter IDs
     */
    public OptimizationResult(List<Assignment> assignments, BigDecimal totalCost, List<Long> selectedTransporters) {
        this(assignments, totalCost, selectedTransporters, true, "Optimization completed successfully");
    }

    /**
     * Constructor for optimization result with custom message.
     *
     * @param assignments          the list of lane assignments
     * @param totalCost           the total cost of all assignments
     * @param selectedTransporters the list of selected transporter IDs
     * @param feasible            whether the optimization was feasible
     * @param message             descriptive message about the result
     */
    public OptimizationResult(List<Assignment> assignments, BigDecimal totalCost, List<Long> selectedTransporters, 
                             boolean feasible, String message) {
        this.assignments = assignments != null ? List.copyOf(assignments) : List.of();
        this.totalCost = totalCost;
        this.selectedTransporters = selectedTransporters != null ? List.copyOf(selectedTransporters) : List.of();
        this.feasible = feasible;
        this.message = message;
    }

    /**
     * Creates an infeasible result.
     *
     * @param message the reason why optimization was not feasible
     * @return infeasible optimization result
     */
    public static OptimizationResult infeasible(String message) {
        return new OptimizationResult(List.of(), BigDecimal.ZERO, List.of(), false, message);
    }

    /**
     * Gets the list of assignments.
     *
     * @return the assignments
     */
    public List<Assignment> getAssignments() {
        return assignments;
    }

    /**
     * Gets the total cost.
     *
     * @return the total cost
     */
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    /**
     * Gets the selected transporters.
     *
     * @return the selected transporter IDs
     */
    public List<Long> getSelectedTransporters() {
        return selectedTransporters;
    }

    /**
     * Checks if the optimization was feasible.
     *
     * @return true if feasible, false otherwise
     */
    public boolean isFeasible() {
        return feasible;
    }

    /**
     * Gets the result message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the number of transporters used.
     *
     * @return the number of transporters
     */
    public int getTransporterCount() {
        return selectedTransporters.size();
    }

    /**
     * Gets the number of lanes assigned.
     *
     * @return the number of lanes
     */
    public int getLaneCount() {
        return assignments.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptimizationResult that = (OptimizationResult) o;
        return feasible == that.feasible &&
                Objects.equals(assignments, that.assignments) &&
                Objects.equals(totalCost, that.totalCost) &&
                Objects.equals(selectedTransporters, that.selectedTransporters) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignments, totalCost, selectedTransporters, feasible, message);
    }

    @Override
    public String toString() {
        return "OptimizationResult{" +
                "assignments=" + assignments.size() + " lanes" +
                ", totalCost=" + totalCost +
                ", selectedTransporters=" + selectedTransporters.size() + " transporters" +
                ", feasible=" + feasible +
                ", message='" + message + '\'' +
                '}';
    }
}
