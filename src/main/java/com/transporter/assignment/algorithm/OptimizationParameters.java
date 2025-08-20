package com.transporter.assignment.algorithm;

import java.util.Objects;

/**
 * Parameters for the optimization algorithm.
 * Contains constraints and preferences for the assignment optimization.
 */
public class OptimizationParameters {

    private final int maxTransporters;
    private final boolean minimizeCost;
    private final boolean maximizeTransporterUsage;
    private final boolean ensureFullCoverage;
    private final long timeoutSeconds;

    /**
     * Constructor with all parameters.
     *
     * @param maxTransporters           maximum number of transporters to use
     * @param minimizeCost             whether to minimize total cost
     * @param maximizeTransporterUsage whether to maximize transporter usage
     * @param ensureFullCoverage       whether to ensure all lanes are covered
     * @param timeoutSeconds           timeout for optimization in seconds
     */
    public OptimizationParameters(int maxTransporters, boolean minimizeCost, boolean maximizeTransporterUsage,
                                 boolean ensureFullCoverage, long timeoutSeconds) {
        if (maxTransporters <= 0) {
            throw new IllegalArgumentException("Max transporters must be positive");
        }
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("Timeout must be positive");
        }

        this.maxTransporters = maxTransporters;
        this.minimizeCost = minimizeCost;
        this.maximizeTransporterUsage = maximizeTransporterUsage;
        this.ensureFullCoverage = ensureFullCoverage;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Creates default optimization parameters.
     *
     * @param maxTransporters the maximum number of transporters
     * @return default parameters with cost minimization and full coverage
     */
    public static OptimizationParameters defaultParameters(int maxTransporters) {
        return new OptimizationParameters(maxTransporters, true, true, true, 30);
    }

    /**
     * Creates parameters for cost minimization only.
     *
     * @param maxTransporters the maximum number of transporters
     * @return parameters optimized for minimum cost
     */
    public static OptimizationParameters costMinimization(int maxTransporters) {
        return new OptimizationParameters(maxTransporters, true, false, true, 30);
    }

    /**
     * Creates parameters for maximum transporter usage.
     *
     * @param maxTransporters the maximum number of transporters
     * @return parameters optimized for maximum transporter usage
     */
    public static OptimizationParameters maxTransporterUsage(int maxTransporters) {
        return new OptimizationParameters(maxTransporters, false, true, true, 30);
    }

    /**
     * Gets the maximum number of transporters.
     *
     * @return the maximum transporters
     */
    public int getMaxTransporters() {
        return maxTransporters;
    }

    /**
     * Checks if cost minimization is enabled.
     *
     * @return true if cost should be minimized
     */
    public boolean isMinimizeCost() {
        return minimizeCost;
    }

    /**
     * Checks if transporter usage maximization is enabled.
     *
     * @return true if transporter usage should be maximized
     */
    public boolean isMaximizeTransporterUsage() {
        return maximizeTransporterUsage;
    }

    /**
     * Checks if full coverage is required.
     *
     * @return true if all lanes must be covered
     */
    public boolean isEnsureFullCoverage() {
        return ensureFullCoverage;
    }

    /**
     * Gets the timeout in seconds.
     *
     * @return the timeout seconds
     */
    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * Creates a copy with different max transporters.
     *
     * @param newMaxTransporters the new maximum transporters
     * @return new parameters with updated max transporters
     */
    public OptimizationParameters withMaxTransporters(int newMaxTransporters) {
        return new OptimizationParameters(newMaxTransporters, minimizeCost, maximizeTransporterUsage,
                ensureFullCoverage, timeoutSeconds);
    }

    /**
     * Creates a copy with different timeout.
     *
     * @param newTimeoutSeconds the new timeout in seconds
     * @return new parameters with updated timeout
     */
    public OptimizationParameters withTimeout(long newTimeoutSeconds) {
        return new OptimizationParameters(maxTransporters, minimizeCost, maximizeTransporterUsage,
                ensureFullCoverage, newTimeoutSeconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptimizationParameters that = (OptimizationParameters) o;
        return maxTransporters == that.maxTransporters &&
                minimizeCost == that.minimizeCost &&
                maximizeTransporterUsage == that.maximizeTransporterUsage &&
                ensureFullCoverage == that.ensureFullCoverage &&
                timeoutSeconds == that.timeoutSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxTransporters, minimizeCost, maximizeTransporterUsage, ensureFullCoverage, timeoutSeconds);
    }

    @Override
    public String toString() {
        return "OptimizationParameters{" +
                "maxTransporters=" + maxTransporters +
                ", minimizeCost=" + minimizeCost +
                ", maximizeTransporterUsage=" + maximizeTransporterUsage +
                ", ensureFullCoverage=" + ensureFullCoverage +
                ", timeoutSeconds=" + timeoutSeconds +
                '}';
    }
}
