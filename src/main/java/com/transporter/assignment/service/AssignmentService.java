package com.transporter.assignment.service;

import com.transporter.assignment.dto.AssignmentRequest;
import com.transporter.assignment.dto.AssignmentResponse;

/**
 * Service interface for transporter assignment optimization.
 */
public interface AssignmentService {

    /**
     * Optimizes the assignment of transporters to lanes based on the given parameters.
     *
     * @param request the assignment request with optimization parameters
     * @return the optimization result with assignments and metadata
     * @throws AssignmentException if there's an error during optimization
     */
    AssignmentResponse optimizeAssignment(AssignmentRequest request) throws AssignmentException;

    /**
     * Validates if assignment optimization is possible with current data.
     *
     * @param request the assignment request
     * @return validation response indicating feasibility
     */
    AssignmentResponse validateAssignment(AssignmentRequest request);

    /**
     * Gets the current optimization capabilities based on available data.
     *
     * @return information about what optimizations are possible
     */
    OptimizationCapabilities getOptimizationCapabilities();
}
