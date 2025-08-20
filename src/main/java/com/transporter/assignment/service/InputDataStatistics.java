package com.transporter.assignment.service;

/**
 * Statistics about the current input data in the system.
 */
public class InputDataStatistics {

    private final long laneCount;
    private final long transporterCount;
    private final long quoteCount;
    private final long lanesWithQuotes;
    private final long transportersWithQuotes;

    /**
     * Constructor with all statistics.
     *
     * @param laneCount              total number of lanes
     * @param transporterCount       total number of transporters
     * @param quoteCount            total number of quotes
     * @param lanesWithQuotes       number of lanes that have at least one quote
     * @param transportersWithQuotes number of transporters that have at least one quote
     */
    public InputDataStatistics(long laneCount, long transporterCount, long quoteCount,
                              long lanesWithQuotes, long transportersWithQuotes) {
        this.laneCount = laneCount;
        this.transporterCount = transporterCount;
        this.quoteCount = quoteCount;
        this.lanesWithQuotes = lanesWithQuotes;
        this.transportersWithQuotes = transportersWithQuotes;
    }

    /**
     * Creates empty statistics.
     *
     * @return statistics with all counts as zero
     */
    public static InputDataStatistics empty() {
        return new InputDataStatistics(0, 0, 0, 0, 0);
    }

    public long getLaneCount() {
        return laneCount;
    }

    public long getTransporterCount() {
        return transporterCount;
    }

    public long getQuoteCount() {
        return quoteCount;
    }

    public long getLanesWithQuotes() {
        return lanesWithQuotes;
    }

    public long getTransportersWithQuotes() {
        return transportersWithQuotes;
    }

    /**
     * Checks if there's any data.
     *
     * @return true if any count is greater than zero
     */
    public boolean hasData() {
        return laneCount > 0 || transporterCount > 0 || quoteCount > 0;
    }

    /**
     * Gets the coverage percentage (lanes with quotes / total lanes).
     *
     * @return coverage percentage (0-100)
     */
    public double getCoveragePercentage() {
        return laneCount > 0 ? (double) lanesWithQuotes / laneCount * 100.0 : 0.0;
    }

    @Override
    public String toString() {
        return "InputDataStatistics{" +
                "laneCount=" + laneCount +
                ", transporterCount=" + transporterCount +
                ", quoteCount=" + quoteCount +
                ", lanesWithQuotes=" + lanesWithQuotes +
                ", transportersWithQuotes=" + transportersWithQuotes +
                ", coverage=" + String.format("%.1f%%", getCoveragePercentage()) +
                '}';
    }
}
