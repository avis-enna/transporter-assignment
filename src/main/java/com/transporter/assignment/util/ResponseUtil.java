package com.transporter.assignment.util;

import com.transporter.assignment.dto.AssignmentDto;
import com.transporter.assignment.dto.AssignmentResponse;
import com.transporter.assignment.dto.InputDataResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Utility class for creating standardized API responses.
 * Reduces code duplication across controllers and services.
 */
public class ResponseUtil {

    private ResponseUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a success response for input data operations.
     */
    public static InputDataResponse inputDataSuccess() {
        return InputDataResponse.success();
    }

    /**
     * Creates a success response for input data operations with custom message.
     */
    public static InputDataResponse inputDataSuccess(String message) {
        return InputDataResponse.success(message);
    }

    /**
     * Creates an error response for input data operations.
     */
    public static InputDataResponse inputDataError(String message) {
        return InputDataResponse.error(message);
    }

    /**
     * Creates a success response for assignment operations.
     */
    public static AssignmentResponse assignmentSuccess(BigDecimal totalCost, 
                                                      List<AssignmentDto> assignments, 
                                                      List<Long> selectedTransporters) {
        return AssignmentResponse.success(totalCost, assignments, selectedTransporters);
    }

    /**
     * Creates a success response for assignment operations with custom message.
     */
    public static AssignmentResponse assignmentSuccess(String message) {
        return AssignmentResponse.success(null, List.of(), List.of()).withMessage(message);
    }

    /**
     * Creates a failure response for assignment operations.
     */
    public static AssignmentResponse assignmentFailure(String message) {
        return AssignmentResponse.failure(message);
    }

    /**
     * Creates an error response for assignment operations.
     */
    public static AssignmentResponse assignmentError(String message) {
        return AssignmentResponse.error(message);
    }

    /**
     * Creates a validation success response.
     */
    public static AssignmentResponse validationSuccess() {
        return assignmentSuccess("Validation passed successfully");
    }

    /**
     * Creates a validation success response with warnings.
     */
    public static AssignmentResponse validationSuccessWithWarnings(List<String> warnings) {
        String message = "Validation passed with warnings: " + String.join("; ", warnings);
        return assignmentSuccess(message);
    }

    /**
     * Creates a validation error response.
     */
    public static AssignmentResponse validationError(String message) {
        return assignmentError("Validation failed: " + message);
    }

    /**
     * Creates a validation error response with multiple errors.
     */
    public static AssignmentResponse validationError(List<String> errors) {
        String message = "Validation failed: " + String.join("; ", errors);
        return assignmentError(message);
    }
}
