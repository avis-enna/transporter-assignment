package com.transporter.assignment.service;

import com.transporter.assignment.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * Base service class providing common functionality for all services.
 * Includes logging, error handling, and validation utilities.
 */
public abstract class BaseService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Executes an operation with error handling and logging.
     *
     * @param operation the operation to execute
     * @param operationName the name of the operation for logging
     * @param <T> the return type
     * @return the result of the operation
     * @throws RuntimeException if the operation fails
     */
    protected <T> T executeWithErrorHandling(Supplier<T> operation, String operationName) {
        try {
            logger.debug("Starting operation: {}", operationName);
            T result = operation.get();
            logger.debug("Completed operation: {}", operationName);
            return result;
        } catch (Exception e) {
            logger.error("Operation failed: {} - {}", operationName, e.getMessage(), e);
            throw new RuntimeException("Failed to " + operationName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Executes an operation with error handling, logging, and custom exception handling.
     *
     * @param operation the operation to execute
     * @param operationName the name of the operation for logging
     * @param exceptionHandler custom exception handler
     * @param <T> the return type
     * @return the result of the operation
     */
    protected <T> T executeWithErrorHandling(Supplier<T> operation, 
                                           String operationName, 
                                           ExceptionHandler<T> exceptionHandler) {
        try {
            logger.debug("Starting operation: {}", operationName);
            T result = operation.get();
            logger.debug("Completed operation: {}", operationName);
            return result;
        } catch (Exception e) {
            logger.error("Operation failed: {} - {}", operationName, e.getMessage(), e);
            return exceptionHandler.handle(e, operationName);
        }
    }

    /**
     * Validates input and throws an exception if validation fails.
     *
     * @param validationErrors list of validation errors
     * @param operationName the name of the operation being validated
     * @throws IllegalArgumentException if validation fails
     */
    protected void validateAndThrow(List<String> validationErrors, String operationName) {
        if (!validationErrors.isEmpty()) {
            String errorMessage = "Validation failed for " + operationName + ": " + 
                                 String.join("; ", validationErrors);
            logger.warn("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Validates input and returns validation result.
     *
     * @param validationErrors list of validation errors
     * @return true if validation passed, false otherwise
     */
    protected boolean isValid(List<String> validationErrors) {
        if (!validationErrors.isEmpty()) {
            logger.debug("Validation failed with {} errors: {}", 
                        validationErrors.size(), validationErrors);
            return false;
        }
        return true;
    }

    /**
     * Logs and validates that a collection is not empty.
     *
     * @param collection the collection to check
     * @param collectionName the name of the collection for logging
     * @throws IllegalArgumentException if the collection is null or empty
     */
    protected void requireNonEmpty(java.util.Collection<?> collection, String collectionName) {
        if (!ValidationUtil.isNotEmpty(collection)) {
            String message = collectionName + " cannot be null or empty";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Logs and validates that a value is not null.
     *
     * @param value the value to check
     * @param valueName the name of the value for logging
     * @throws IllegalArgumentException if the value is null
     */
    protected void requireNonNull(Object value, String valueName) {
        if (value == null) {
            String message = valueName + " cannot be null";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Logs and validates that a number is positive.
     *
     * @param number the number to check
     * @param numberName the name of the number for logging
     * @throws IllegalArgumentException if the number is not positive
     */
    protected void requirePositive(Number number, String numberName) {
        if (!ValidationUtil.isPositive(number)) {
            String message = numberName + " must be positive";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Logs an info message with operation context.
     *
     * @param message the message to log
     * @param args message arguments
     */
    protected void logInfo(String message, Object... args) {
        logger.info(message, args);
    }

    /**
     * Logs a debug message with operation context.
     *
     * @param message the message to log
     * @param args message arguments
     */
    protected void logDebug(String message, Object... args) {
        logger.debug(message, args);
    }

    /**
     * Logs a warning message with operation context.
     *
     * @param message the message to log
     * @param args message arguments
     */
    protected void logWarn(String message, Object... args) {
        logger.warn(message, args);
    }

    /**
     * Logs an error message with operation context.
     *
     * @param message the message to log
     * @param args message arguments
     */
    protected void logError(String message, Object... args) {
        logger.error(message, args);
    }

    /**
     * Functional interface for custom exception handling.
     */
    @FunctionalInterface
    protected interface ExceptionHandler<T> {
        T handle(Exception exception, String operationName);
    }
}
