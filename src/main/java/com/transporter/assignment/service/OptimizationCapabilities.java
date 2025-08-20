package com.transporter.assignment.service;

import java.util.List;

/**
 * Info about what we can optimize
 */
public class OptimizationCapabilities {

    private final boolean canOptimize;
    private final int maxPossibleTransporters;
    private final int minRequiredTransporters;
    private final List<String> limitations;
    private final List<String> recommendations;

    /**
     * Constructor
     */
    public OptimizationCapabilities(boolean canOptimize, int maxPossibleTransporters, int minRequiredTransporters,
                                   List<String> limitations, List<String> recommendations) {
        this.canOptimize = canOptimize;
        this.maxPossibleTransporters = maxPossibleTransporters;
        this.minRequiredTransporters = minRequiredTransporters;
        this.limitations = limitations != null ? List.copyOf(limitations) : List.of();
        this.recommendations = recommendations != null ? List.copyOf(recommendations) : List.of();
    }

    /**
     * Creates capabilities for when no optimization is possible.
     *
     * @param reason the reason why optimization is not possible
     * @return capabilities indicating no optimization possible
     */
    public static OptimizationCapabilities notPossible(String reason) {
        return new OptimizationCapabilities(false, 0, 0, List.of(reason), List.of());
    }

    /**
     * Creates capabilities for when optimization is possible.
     *
     * @param maxTransporters max transporters available
     * @param minTransporters min transporters required
     * @return capabilities indicating optimization is possible
     */
    public static OptimizationCapabilities possible(int maxTransporters, int minTransporters) {
        return new OptimizationCapabilities(true, maxTransporters, minTransporters, List.of(), List.of());
    }

    public boolean canOptimize() {
        return canOptimize;
    }

    public boolean getCanOptimize() {
        return canOptimize;
    }

    public int getMaxPossibleTransporters() {
        return maxPossibleTransporters;
    }

    public int getMinRequiredTransporters() {
        return minRequiredTransporters;
    }

    public List<String> getLimitations() {
        return limitations;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    /**
     * Checks if the given number of transporters is valid.
     *
     * @param requestedTransporters the requested number of transporters
     * @return true if valid, false otherwise
     */
    public boolean isValidTransporterCount(int requestedTransporters) {
        return canOptimize && 
               requestedTransporters >= minRequiredTransporters && 
               requestedTransporters <= maxPossibleTransporters;
    }

    @Override
    public String toString() {
        return "OptimizationCapabilities{" +
                "canOptimize=" + canOptimize +
                ", maxPossibleTransporters=" + maxPossibleTransporters +
                ", minRequiredTransporters=" + minRequiredTransporters +
                ", limitations=" + limitations.size() +
                ", recommendations=" + recommendations.size() +
                '}';
    }
}
