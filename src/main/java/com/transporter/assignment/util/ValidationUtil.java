package com.transporter.assignment.util;

import com.transporter.assignment.dto.InputDataRequest;
import com.transporter.assignment.dto.LaneDto;
import com.transporter.assignment.dto.LaneQuoteDto;
import com.transporter.assignment.dto.TransporterDto;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for common validation operations.
 * Centralizes validation logic to reduce duplication.
 */
public class ValidationUtil {

    private ValidationUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Validates input data request and returns list of validation errors.
     */
    public static List<String> validateInputData(InputDataRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("Input data request cannot be null");
            return errors;
        }

        // Validate lanes
        errors.addAll(validateLanes(request.getLanes()));

        // Validate transporters
        errors.addAll(validateTransporters(request.getTransporters()));

        // Validate that quoted lanes exist
        errors.addAll(validateQuotedLanesExist(request.getLanes(), request.getTransporters()));

        // Validate lane coverage
        errors.addAll(validateLaneCoverage(request.getLanes(), request.getTransporters()));

        return errors;
    }

    /**
     * Validates lanes and returns list of validation errors.
     */
    public static List<String> validateLanes(List<LaneDto> lanes) {
        List<String> errors = new ArrayList<>();

        if (lanes == null || lanes.isEmpty()) {
            errors.add("At least one lane must be provided");
            return errors;
        }

        Set<Long> laneIds = new HashSet<>();
        Set<String> laneRoutes = new HashSet<>();

        for (LaneDto lane : lanes) {
            // Validate lane ID
            if (lane.getId() == null || lane.getId() <= 0) {
                errors.add("Lane ID must be a positive number");
            } else if (!laneIds.add(lane.getId())) {
                errors.add("Duplicate lane ID: " + lane.getId());
            }

            // Validate origin and destination
            if (isBlankOrNull(lane.getOrigin())) {
                errors.add("Lane origin cannot be blank");
            }
            if (isBlankOrNull(lane.getDestination())) {
                errors.add("Lane destination cannot be blank");
            }
            if (lane.getOrigin() != null && lane.getDestination() != null && 
                lane.getOrigin().equals(lane.getDestination())) {
                errors.add("Lane origin and destination cannot be the same");
            }

            // Check for duplicate routes
            String route = lane.getOrigin() + "->" + lane.getDestination();
            if (!laneRoutes.add(route)) {
                errors.add("Duplicate route: " + route);
            }
        }

        return errors;
    }

    /**
     * Validates transporters and returns list of validation errors.
     */
    public static List<String> validateTransporters(List<TransporterDto> transporters) {
        List<String> errors = new ArrayList<>();

        if (transporters == null || transporters.isEmpty()) {
            errors.add("At least one transporter must be provided");
            return errors;
        }

        Set<Long> transporterIds = new HashSet<>();
        Set<String> transporterNames = new HashSet<>();

        for (TransporterDto transporter : transporters) {
            // Validate transporter ID
            if (transporter.getId() == null || transporter.getId() <= 0) {
                errors.add("Transporter ID must be a positive number");
            } else if (!transporterIds.add(transporter.getId())) {
                errors.add("Duplicate transporter ID: " + transporter.getId());
            }

            // Validate transporter name
            if (isBlankOrNull(transporter.getName())) {
                errors.add("Transporter name cannot be blank");
            } else if (!transporterNames.add(transporter.getName())) {
                errors.add("Duplicate transporter name: " + transporter.getName());
            }

            // Validate quotes
            errors.addAll(validateQuotes(transporter.getId(), transporter.getLaneQuotes()));
        }

        return errors;
    }

    /**
     * Validates quotes for a transporter.
     */
    public static List<String> validateQuotes(Long transporterId, List<LaneQuoteDto> quotes) {
        List<String> errors = new ArrayList<>();

        if (quotes == null) {
            return errors; // Empty quotes are allowed
        }

        Set<Long> quotedLanes = new HashSet<>();

        for (LaneQuoteDto quote : quotes) {
            // Validate lane ID
            if (quote.getLaneId() == null || quote.getLaneId() <= 0) {
                errors.add("Quote lane ID must be a positive number for transporter " + transporterId);
            } else if (!quotedLanes.add(quote.getLaneId())) {
                errors.add("Duplicate quote for lane " + quote.getLaneId() + " by transporter " + transporterId);
            }

            // Validate quote amount
            if (quote.getQuote() == null || quote.getQuote().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Quote amount must be positive for lane " + quote.getLaneId() + " by transporter " + transporterId);
            }
        }

        return errors;
    }

    /**
     * Validates that all quoted lanes exist in the lanes list.
     */
    public static List<String> validateQuotedLanesExist(List<LaneDto> lanes, List<TransporterDto> transporters) {
        List<String> errors = new ArrayList<>();

        if (lanes == null || transporters == null) {
            return errors;
        }

        Set<Long> laneIds = lanes.stream()
                .map(LaneDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (TransporterDto transporter : transporters) {
            if (transporter.getLaneQuotes() != null) {
                for (LaneQuoteDto quote : transporter.getLaneQuotes()) {
                    if (quote.getLaneId() != null && !laneIds.contains(quote.getLaneId())) {
                        errors.add("Transporter " + transporter.getId() +
                                  " has quote for non-existent lane: " + quote.getLaneId());
                    }
                }
            }
        }

        return errors;
    }

    /**
     * Validates that all lanes have at least one quote.
     */
    public static List<String> validateLaneCoverage(List<LaneDto> lanes, List<TransporterDto> transporters) {
        List<String> errors = new ArrayList<>();

        if (lanes == null || transporters == null) {
            return errors;
        }

        Set<Long> laneIds = lanes.stream()
                .map(LaneDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> quotedLanes = transporters.stream()
                .filter(t -> t.getLaneQuotes() != null)
                .flatMap(t -> t.getLaneQuotes().stream())
                .map(LaneQuoteDto::getLaneId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Long laneId : laneIds) {
            if (!quotedLanes.contains(laneId)) {
                errors.add("Lane " + laneId + " has no quotes from any transporter");
            }
        }

        return errors;
    }

    /**
     * Validates assignment request parameters.
     */
    public static List<String> validateAssignmentRequest(Integer maxTransporters, int totalTransporters) {
        List<String> errors = new ArrayList<>();

        if (maxTransporters == null || maxTransporters <= 0) {
            errors.add("Maximum transporters must be a positive number");
        } else if (maxTransporters > totalTransporters) {
            errors.add("Maximum transporters (" + maxTransporters + 
                      ") cannot exceed total available transporters (" + totalTransporters + ")");
        }

        return errors;
    }

    /**
     * Checks if a string is null or blank.
     */
    private static boolean isBlankOrNull(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates that a collection is not null or empty.
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Validates that a value is positive.
     */
    public static boolean isPositive(Number number) {
        return number != null && number.doubleValue() > 0;
    }

    /**
     * Validates that a BigDecimal is positive.
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }
}
