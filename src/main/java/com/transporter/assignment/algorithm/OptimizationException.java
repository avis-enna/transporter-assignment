package com.transporter.assignment.algorithm;

/**
 * Exception thrown when optimization algorithm encounters an error.
 */
public class OptimizationException extends Exception {

    /**
     * Constructs a new optimization exception with the specified detail message.
     *
     * @param message the detail message
     */
    public OptimizationException(String message) {
        super(message);
    }

    /**
     * Constructs a new optimization exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public OptimizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
