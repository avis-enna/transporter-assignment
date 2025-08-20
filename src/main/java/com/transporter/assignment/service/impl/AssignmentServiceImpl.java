package com.transporter.assignment.service.impl;

import com.transporter.assignment.algorithm.OptimizationException;
import com.transporter.assignment.algorithm.OptimizationParameters;
import com.transporter.assignment.algorithm.OptimizationResult;
import com.transporter.assignment.algorithm.TransporterAssignmentOptimizer;
import com.transporter.assignment.dto.AssignmentDto;
import com.transporter.assignment.dto.AssignmentRequest;
import com.transporter.assignment.dto.AssignmentResponse;
import com.transporter.assignment.model.Assignment;
import com.transporter.assignment.model.Lane;
import com.transporter.assignment.model.LaneQuote;
import com.transporter.assignment.repository.LaneQuoteRepository;
import com.transporter.assignment.repository.LaneRepository;
import com.transporter.assignment.repository.TransporterRepository;
import com.transporter.assignment.service.AssignmentException;
import com.transporter.assignment.service.AssignmentService;
import com.transporter.assignment.service.OptimizationCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of AssignmentService for transporter assignment optimization.
 */
@Service
@Transactional(readOnly = true)
public class AssignmentServiceImpl implements AssignmentService {

    private final LaneRepository laneRepository;
    private final TransporterRepository transporterRepository;
    private final LaneQuoteRepository laneQuoteRepository;
    private final TransporterAssignmentOptimizer optimizer;

    @Autowired
    public AssignmentServiceImpl(LaneRepository laneRepository,
                                TransporterRepository transporterRepository,
                                LaneQuoteRepository laneQuoteRepository,
                                TransporterAssignmentOptimizer optimizer) {
        this.laneRepository = laneRepository;
        this.transporterRepository = transporterRepository;
        this.laneQuoteRepository = laneQuoteRepository;
        this.optimizer = optimizer;
    }

    @Override
    public AssignmentResponse optimizeAssignment(AssignmentRequest request) throws AssignmentException {
        try {
            // Validate that we have data
            if (!hasRequiredData()) {
                return AssignmentResponse.error("No input data available. Please submit lanes and transporters first.");
            }

            // Get all lanes and quotes
            List<Lane> lanes = laneRepository.findAll();
            List<LaneQuote> laneQuotes = laneQuoteRepository.findAll();

            if (lanes.isEmpty()) {
                return AssignmentResponse.error("No lanes available for assignment.");
            }

            if (laneQuotes.isEmpty()) {
                return AssignmentResponse.error("No quotes available for assignment.");
            }

            // Create optimization parameters
            OptimizationParameters parameters = new OptimizationParameters(
                    request.getMaxTransporters(),
                    request.isMinimizeCost(),
                    request.isMaximizeTransporterUsage(),
                    request.isEnsureFullCoverage(),
                    request.getTimeoutSeconds()
            );

            // Run optimization
            OptimizationResult result = optimizer.optimize(lanes, laneQuotes, parameters);

            if (!result.isFeasible()) {
                return AssignmentResponse.failure(result.getMessage());
            }

            // Convert to DTOs
            List<AssignmentDto> assignmentDtos = result.getAssignments().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return AssignmentResponse.success(result.getTotalCost(), assignmentDtos, result.getSelectedTransporters());

        } catch (OptimizationException e) {
            throw new AssignmentException("Optimization failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AssignmentException("Unexpected error during assignment: " + e.getMessage(), e);
        }
    }

    @Override
    public AssignmentResponse validateAssignment(AssignmentRequest request) {
        try {
            // Check if we have data
            if (!hasRequiredData()) {
                return AssignmentResponse.error("No input data available for validation.");
            }

            // Get optimization capabilities
            OptimizationCapabilities capabilities = getOptimizationCapabilities();

            if (!capabilities.canOptimize()) {
                return AssignmentResponse.error("Assignment optimization is not possible: " + 
                        String.join(", ", capabilities.getLimitations()));
            }

            // Validate the requested number of transporters
            if (!capabilities.isValidTransporterCount(request.getMaxTransporters())) {
                return AssignmentResponse.error(
                        String.format("Invalid number of transporters. Requested: %d, " +
                                     "Valid range: %d-%d",
                                     request.getMaxTransporters(),
                                     capabilities.getMinRequiredTransporters(),
                                     capabilities.getMaxPossibleTransporters()));
            }

            // Validate optimization parameters
            List<Lane> lanes = laneRepository.findAll();
            List<LaneQuote> laneQuotes = laneQuoteRepository.findAll();

            OptimizationParameters parameters = new OptimizationParameters(
                    request.getMaxTransporters(),
                    request.isMinimizeCost(),
                    request.isMaximizeTransporterUsage(),
                    request.isEnsureFullCoverage(),
                    request.getTimeoutSeconds()
            );

            var validationResult = optimizer.validate(lanes, laneQuotes, parameters);

            if (!validationResult.isValid()) {
                return AssignmentResponse.error("Validation failed: " + 
                        String.join(", ", validationResult.getErrors()));
            }

            String message = "Assignment validation passed.";
            if (validationResult.hasWarnings()) {
                message += " Warnings: " + String.join(", ", validationResult.getWarnings());
            }

            return AssignmentResponse.success(null, List.of(), List.of()).withMessage(message);

        } catch (Exception e) {
            return AssignmentResponse.error("Validation error: " + e.getMessage());
        }
    }

    @Override
    public OptimizationCapabilities getOptimizationCapabilities() {
        try {
            if (!hasRequiredData()) {
                return OptimizationCapabilities.notPossible("No input data available");
            }

            List<Lane> lanes = laneRepository.findAll();
            List<LaneQuote> laneQuotes = laneQuoteRepository.findAll();

            if (lanes.isEmpty()) {
                return OptimizationCapabilities.notPossible("No lanes available");
            }

            if (laneQuotes.isEmpty()) {
                return OptimizationCapabilities.notPossible("No quotes available");
            }

            // Check if all lanes have at least one quote
            Set<Long> laneIds = lanes.stream().map(Lane::getId).collect(Collectors.toSet());
            Set<Long> quotedLaneIds = laneQuotes.stream().map(LaneQuote::getLaneId).collect(Collectors.toSet());

            if (!quotedLaneIds.containsAll(laneIds)) {
                Set<Long> uncoveredLanes = new HashSet<>(laneIds);
                uncoveredLanes.removeAll(quotedLaneIds);
                return OptimizationCapabilities.notPossible(
                        "Some lanes have no quotes: " + uncoveredLanes);
            }

            // Calculate capabilities
            Set<Long> availableTransporters = laneQuotes.stream()
                    .map(LaneQuote::getTransporterId)
                    .collect(Collectors.toSet());

            int maxPossibleTransporters = availableTransporters.size();
            int minRequiredTransporters = calculateMinRequiredTransporters(lanes, laneQuotes);

            List<String> limitations = new ArrayList<>();
            List<String> recommendations = new ArrayList<>();

            // Add recommendations based on data analysis
            if (maxPossibleTransporters > 10) {
                recommendations.add("Consider using fewer transporters for better performance");
            }

            double avgQuotesPerLane = (double) laneQuotes.size() / lanes.size();
            if (avgQuotesPerLane < 2.0) {
                recommendations.add("More quotes per lane would provide better optimization options");
            }

            return new OptimizationCapabilities(true, maxPossibleTransporters, minRequiredTransporters,
                    limitations, recommendations);

        } catch (Exception e) {
            return OptimizationCapabilities.notPossible("Error analyzing capabilities: " + e.getMessage());
        }
    }

    private boolean hasRequiredData() {
        return laneRepository.count() > 0 && 
               transporterRepository.count() > 0 && 
               laneQuoteRepository.count() > 0;
    }

    private AssignmentDto convertToDto(Assignment assignment) {
        return new AssignmentDto(assignment.getLaneId(), assignment.getTransporterId(), assignment.getCost());
    }

    private int calculateMinRequiredTransporters(List<Lane> lanes, List<LaneQuote> laneQuotes) {
        // This is a simplified calculation
        // In a more sophisticated implementation, we could use a greedy set cover algorithm
        Set<Long> transporterIds = laneQuotes.stream()
                .map(LaneQuote::getTransporterId)
                .collect(Collectors.toSet());

        // For now, return 1 as minimum (assuming at least one transporter can cover all lanes)
        // This could be enhanced with actual set cover calculation
        return Math.min(1, transporterIds.size());
    }
}
