package com.transporter.assignment.algorithm;

import com.transporter.assignment.model.Lane;
import com.transporter.assignment.model.LaneQuote;

import java.util.List;

/**
 * Interface for transporter assignment optimization algorithms.
 * Defines the contract for optimizing transporter assignments to lanes.
 */
public interface TransporterAssignmentOptimizer {

    /**
     * Optimizes the assignment of transporters to lanes.
     *
     * @param lanes      the list of lanes to be assigned
     * @param laneQuotes the list of available quotes from transporters
     * @param parameters the optimization parameters and constraints
     * @return the optimization result containing assignments and metadata
     * @throws OptimizationException if optimization fails due to algorithm errors
     */
    OptimizationResult optimize(List<Lane> lanes, List<LaneQuote> laneQuotes, OptimizationParameters parameters)
            throws OptimizationException;

    /**
     * Validates if the given lanes and quotes can potentially be optimized.
     *
     * @param lanes      the list of lanes
     * @param laneQuotes the list of lane quotes
     * @param parameters the optimization parameters
     * @return validation result with details about feasibility
     */
    ValidationResult validate(List<Lane> lanes, List<LaneQuote> laneQuotes, OptimizationParameters parameters);

    /**
     * Gets the name of this optimization algorithm.
     *
     * @return the algorithm name
     */
    String getAlgorithmName();

    /**
     * Checks if this optimizer supports the given parameters.
     *
     * @param parameters the optimization parameters
     * @return true if parameters are supported, false otherwise
     */
    boolean supportsParameters(OptimizationParameters parameters);
}
