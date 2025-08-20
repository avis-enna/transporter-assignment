package com.transporter.assignment.algorithm;

import com.transporter.assignment.model.Assignment;
import com.transporter.assignment.model.Lane;
import com.transporter.assignment.model.LaneQuote;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Greedy optimization algorithm for transporter assignment.
 * Tries to find a good solution by being smart about which transporters to pick.
 */
@Component
public class GreedyTransporterOptimizer implements TransporterAssignmentOptimizer {

    @Override
    public OptimizationResult optimize(List<Lane> lanes, List<LaneQuote> laneQuotes, OptimizationParameters parameters)
            throws OptimizationException {
        
        // Basic validation first
        ValidationResult validation = validate(lanes, laneQuotes, parameters);
        if (!validation.isValid()) {
            return OptimizationResult.infeasible("Validation failed: " + String.join(", ", validation.getErrors()));
        }

        try {
            // Group quotes by lane - makes lookups faster
            Map<Long, List<LaneQuote>> quotesByLane = laneQuotes.stream()
                    .collect(Collectors.groupingBy(LaneQuote::getLaneId));

            // Make sure all lanes have at least one quote
            Set<Long> laneIds = lanes.stream().map(Lane::getId).collect(Collectors.toSet());
            Set<Long> quotedLaneIds = quotesByLane.keySet();
            
            if (!quotedLaneIds.containsAll(laneIds)) {
                Set<Long> uncoveredLanes = new HashSet<>(laneIds);
                uncoveredLanes.removeAll(quotedLaneIds);
                return OptimizationResult.infeasible("No quotes available for lanes: " + uncoveredLanes);
            }

            // Now try to find the best assignment
            return findOptimalAssignment(lanes, quotesByLane, parameters);

        } catch (Exception e) {
            throw new OptimizationException("Optimization failed: " + e.getMessage(), e);
        }
    }

    @Override
    public ValidationResult validate(List<Lane> lanes, List<LaneQuote> laneQuotes, OptimizationParameters parameters) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Validate inputs
        if (lanes == null || lanes.isEmpty()) {
            errors.add("Lanes list cannot be null or empty");
        }
        if (laneQuotes == null || laneQuotes.isEmpty()) {
            errors.add("Lane quotes list cannot be null or empty");
        }
        if (parameters == null) {
            errors.add("Optimization parameters cannot be null");
        }

        if (!errors.isEmpty()) {
            return ValidationResult.invalid(errors);
        }

        // Check for duplicate lanes
        Set<Long> uniqueLaneIds = lanes.stream().map(Lane::getId).collect(Collectors.toSet());
        if (uniqueLaneIds.size() != lanes.size()) {
            errors.add("Duplicate lanes found in input");
        }

        // Check if we have enough transporters
        Set<Long> availableTransporters = laneQuotes.stream()
                .map(LaneQuote::getTransporterId)
                .collect(Collectors.toSet());
        
        if (availableTransporters.size() < parameters.getMaxTransporters()) {
            warnings.add("Available transporters (" + availableTransporters.size() + 
                        ") is less than max transporters (" + parameters.getMaxTransporters() + ")");
        }

        // Check lane coverage
        Set<Long> laneIds = lanes.stream().map(Lane::getId).collect(Collectors.toSet());
        Set<Long> quotedLaneIds = laneQuotes.stream().map(LaneQuote::getLaneId).collect(Collectors.toSet());
        
        Set<Long> uncoveredLanes = new HashSet<>(laneIds);
        uncoveredLanes.removeAll(quotedLaneIds);
        
        if (!uncoveredLanes.isEmpty()) {
            errors.add("No quotes available for lanes: " + uncoveredLanes);
        }

        if (!errors.isEmpty()) {
            return ValidationResult.invalid(errors, warnings);
        }
        
        return warnings.isEmpty() ? ValidationResult.valid() : ValidationResult.validWithWarnings(warnings);
    }

    @Override
    public String getAlgorithmName() {
        return "Greedy Transporter Optimizer";
    }

    @Override
    public boolean supportsParameters(OptimizationParameters parameters) {
        return parameters != null && 
               parameters.getMaxTransporters() > 0 && 
               parameters.getTimeoutSeconds() > 0;
    }

    private OptimizationResult findOptimalAssignment(List<Lane> lanes, 
                                                   Map<Long, List<LaneQuote>> quotesByLane,
                                                   OptimizationParameters parameters) {
        
        // Get all available transporters
        Set<Long> availableTransporters = quotesByLane.values().stream()
                .flatMap(List::stream)
                .map(LaneQuote::getTransporterId)
                .collect(Collectors.toSet());

        OptimizationResult bestResult = null;

        // For small datasets, we can try all combinations - brute force but guaranteed optimal
        if (availableTransporters.size() <= 10 && parameters.getMaxTransporters() <= 5) {
            bestResult = tryAllCombinations(lanes, quotesByLane, availableTransporters, parameters);
        } else {
            // For larger datasets, use greedy approach - faster but might not be perfect
            bestResult = greedyAssignment(lanes, quotesByLane, availableTransporters, parameters);
        }

        return bestResult != null ? bestResult : 
               OptimizationResult.infeasible("No feasible assignment found");
    }

    private OptimizationResult tryAllCombinations(List<Lane> lanes,
                                                Map<Long, List<LaneQuote>> quotesByLane,
                                                Set<Long> availableTransporters,
                                                OptimizationParameters parameters) {
        
        OptimizationResult bestResult = null;

        // Try combinations from 1 to maxTransporters
        for (int numTransporters = 1; numTransporters <= Math.min(parameters.getMaxTransporters(), availableTransporters.size()); numTransporters++) {
            
            // Generate all combinations of transporters
            List<Set<Long>> combinations = generateCombinations(new ArrayList<>(availableTransporters), numTransporters);
            
            for (Set<Long> transporterSet : combinations) {
                OptimizationResult result = assignWithTransporters(lanes, quotesByLane, transporterSet, parameters);
                
                if (result.isFeasible()) {
                    if (bestResult == null || isBetterResult(result, bestResult, parameters)) {
                        bestResult = result;
                    }
                }
            }
        }

        return bestResult;
    }

    private OptimizationResult greedyAssignment(List<Lane> lanes,
                                              Map<Long, List<LaneQuote>> quotesByLane,
                                              Set<Long> availableTransporters,
                                              OptimizationParameters parameters) {
        
        // Start with picking transporters that can cover the most lanes cheaply
        List<Long> selectedTransporters = new ArrayList<>();
        Set<Long> coveredLanes = new HashSet<>();
        
        while (selectedTransporters.size() < parameters.getMaxTransporters() && 
               coveredLanes.size() < lanes.size()) {
            
            // Find the next best transporter to add
            Long bestTransporter = findBestNextTransporter(lanes, quotesByLane, 
                                                         availableTransporters, selectedTransporters, 
                                                         coveredLanes, parameters);
            
            if (bestTransporter == null) {
                break; // Can't find any more useful transporters
            }
            
            selectedTransporters.add(bestTransporter);
            availableTransporters.remove(bestTransporter);
            
            // Update which lanes we can now cover
            for (Lane lane : lanes) {
                List<LaneQuote> quotes = quotesByLane.get(lane.getId());
                if (quotes != null && quotes.stream().anyMatch(q -> q.getTransporterId().equals(bestTransporter))) {
                    coveredLanes.add(lane.getId());
                }
            }
        }
        
        return assignWithTransporters(lanes, quotesByLane, new HashSet<>(selectedTransporters), parameters);
    }

    private Long findBestNextTransporter(List<Lane> lanes,
                                       Map<Long, List<LaneQuote>> quotesByLane,
                                       Set<Long> availableTransporters,
                                       List<Long> selectedTransporters,
                                       Set<Long> coveredLanes,
                                       OptimizationParameters parameters) {
        
        Long bestTransporter = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        
        for (Long transporterId : availableTransporters) {
            if (selectedTransporters.contains(transporterId)) {
                continue;
            }
            
            double score = calculateTransporterScore(transporterId, lanes, quotesByLane, 
                                                   coveredLanes, parameters);
            
            if (score > bestScore) {
                bestScore = score;
                bestTransporter = transporterId;
            }
        }
        
        return bestTransporter;
    }

    private double calculateTransporterScore(Long transporterId,
                                           List<Lane> lanes,
                                           Map<Long, List<LaneQuote>> quotesByLane,
                                           Set<Long> coveredLanes,
                                           OptimizationParameters parameters) {
        
        int newLanesCovered = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalQuotes = 0;
        
        // TODO: maybe we can optimize this loop somehow?
        for (Lane lane : lanes) {
            List<LaneQuote> quotes = quotesByLane.get(lane.getId());
            if (quotes != null) {
                Optional<LaneQuote> transporterQuote = quotes.stream()
                        .filter(q -> q.getTransporterId().equals(transporterId))
                        .findFirst();
                
                if (transporterQuote.isPresent()) {
                    totalQuotes++;
                    totalCost = totalCost.add(transporterQuote.get().getQuote());
                    
                    if (!coveredLanes.contains(lane.getId())) {
                        newLanesCovered++;
                    }
                }
            }
        }
        
        if (totalQuotes == 0) {
            return Double.NEGATIVE_INFINITY;
        }
        
        // Score based on new lanes covered and cost efficiency
        double score = newLanesCovered * 1000.0; // Prioritize coverage
        
        if (parameters.isMinimizeCost()) {
            double avgCost = totalCost.doubleValue() / totalQuotes;
            score -= avgCost / 100.0; // Penalize high costs
        }
        
        if (parameters.isMaximizeTransporterUsage()) {
            score += totalQuotes * 10.0; // Reward transporters that can handle more lanes
        }
        
        return score;
    }

    private OptimizationResult assignWithTransporters(List<Lane> lanes,
                                                    Map<Long, List<LaneQuote>> quotesByLane,
                                                    Set<Long> selectedTransporters,
                                                    OptimizationParameters parameters) {

        List<Assignment> assignments = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Lane lane : lanes) {
            List<LaneQuote> quotes = quotesByLane.get(lane.getId());
            if (quotes == null) {
                return OptimizationResult.infeasible("No quotes for lane " + lane.getId());
            }

            // Find the best quote from selected transporters
            Optional<LaneQuote> bestQuote = quotes.stream()
                    .filter(q -> selectedTransporters.contains(q.getTransporterId()))
                    .min(Comparator.comparing(LaneQuote::getQuote));

            if (bestQuote.isEmpty()) {
                return OptimizationResult.infeasible("No quotes from selected transporters for lane " + lane.getId());
            }

            Assignment assignment = Assignment.fromLaneQuote(bestQuote.get());
            assignments.add(assignment);
            totalCost = totalCost.add(bestQuote.get().getQuote());
        }

        return new OptimizationResult(assignments, totalCost, new ArrayList<>(selectedTransporters));
    }

    private boolean isBetterResult(OptimizationResult candidate, OptimizationResult current, OptimizationParameters parameters) {
        if (!candidate.isFeasible()) {
            return false;
        }
        if (!current.isFeasible()) {
            return true;
        }

        // Primary: Prefer solutions that use more transporters (if maximizing usage)
        if (parameters.isMaximizeTransporterUsage()) {
            if (candidate.getTransporterCount() != current.getTransporterCount()) {
                return candidate.getTransporterCount() > current.getTransporterCount();
            }
        }

        // Secondary: Prefer lower cost (if minimizing cost)
        if (parameters.isMinimizeCost()) {
            return candidate.getTotalCost().compareTo(current.getTotalCost()) < 0;
        }

        // Default: prefer lower cost
        return candidate.getTotalCost().compareTo(current.getTotalCost()) < 0;
    }

    private List<Set<Long>> generateCombinations(List<Long> elements, int k) {
        List<Set<Long>> combinations = new ArrayList<>();
        generateCombinationsHelper(elements, k, 0, new HashSet<>(), combinations);
        return combinations;
    }

    private void generateCombinationsHelper(List<Long> elements, int k, int start,
                                          Set<Long> current, List<Set<Long>> combinations) {
        if (current.size() == k) {
            combinations.add(new HashSet<>(current));
            return;
        }

        for (int i = start; i < elements.size(); i++) {
            current.add(elements.get(i));
            generateCombinationsHelper(elements, k, i + 1, current, combinations);
            current.remove(elements.get(i));
        }
    }
}
