package com.transporter.assignment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request DTO for assignment optimization.
 */
public class AssignmentRequest {

    @Min(value = 1, message = "Max transporters must be at least 1")
    @Max(value = 100, message = "Max transporters cannot exceed 100")
    private int maxTransporters;

    private boolean minimizeCost = true;
    private boolean maximizeTransporterUsage = true;
    private boolean ensureFullCoverage = true;
    private long timeoutSeconds = 30;

    /**
     * Default constructor.
     */
    public AssignmentRequest() {
    }

    /**
     * Constructor with max transporters.
     *
     * @param maxTransporters the maximum number of transporters to use
     */
    public AssignmentRequest(int maxTransporters) {
        this.maxTransporters = maxTransporters;
    }

    /**
     * Constructor with all fields.
     *
     * @param maxTransporters           maximum number of transporters
     * @param minimizeCost             whether to minimize cost
     * @param maximizeTransporterUsage whether to maximize transporter usage
     * @param ensureFullCoverage       whether to ensure full coverage
     * @param timeoutSeconds           timeout in seconds
     */
    public AssignmentRequest(int maxTransporters, boolean minimizeCost, boolean maximizeTransporterUsage,
                           boolean ensureFullCoverage, long timeoutSeconds) {
        this.maxTransporters = maxTransporters;
        this.minimizeCost = minimizeCost;
        this.maximizeTransporterUsage = maximizeTransporterUsage;
        this.ensureFullCoverage = ensureFullCoverage;
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getMaxTransporters() {
        return maxTransporters;
    }

    public void setMaxTransporters(int maxTransporters) {
        this.maxTransporters = maxTransporters;
    }

    public boolean isMinimizeCost() {
        return minimizeCost;
    }

    public void setMinimizeCost(boolean minimizeCost) {
        this.minimizeCost = minimizeCost;
    }

    public boolean isMaximizeTransporterUsage() {
        return maximizeTransporterUsage;
    }

    public void setMaximizeTransporterUsage(boolean maximizeTransporterUsage) {
        this.maximizeTransporterUsage = maximizeTransporterUsage;
    }

    public boolean isEnsureFullCoverage() {
        return ensureFullCoverage;
    }

    public void setEnsureFullCoverage(boolean ensureFullCoverage) {
        this.ensureFullCoverage = ensureFullCoverage;
    }

    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(long timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public String toString() {
        return "AssignmentRequest{" +
                "maxTransporters=" + maxTransporters +
                ", minimizeCost=" + minimizeCost +
                ", maximizeTransporterUsage=" + maximizeTransporterUsage +
                ", ensureFullCoverage=" + ensureFullCoverage +
                ", timeoutSeconds=" + timeoutSeconds +
                '}';
    }
}
