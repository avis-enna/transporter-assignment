package com.transporter.assignment.service;

/**
 * Exception thrown when there's an error during assignment optimization.
 */
public class AssignmentException extends Exception {

    /**
     * Constructs a new assignment exception with the specified detail message.
     *
     * @param message the detail message
     */
    public AssignmentException(String message) {
        super(message);
    }

    /**
     * Constructs a new assignment exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public AssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
