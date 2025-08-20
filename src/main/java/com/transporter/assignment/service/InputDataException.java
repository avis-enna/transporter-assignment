package com.transporter.assignment.service;

/**
 * Exception thrown when there's an error processing input data.
 */
public class InputDataException extends Exception {

    /**
     * Constructs a new input data exception with the specified detail message.
     *
     * @param message the detail message
     */
    public InputDataException(String message) {
        super(message);
    }

    /**
     * Constructs a new input data exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public InputDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
