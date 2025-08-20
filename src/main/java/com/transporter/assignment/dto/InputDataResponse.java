package com.transporter.assignment.dto;

/**
 * Response DTO for input data submission.
 */
public class InputDataResponse {

    private String status;
    private String message;

    /**
     * Default constructor.
     */
    public InputDataResponse() {
    }

    /**
     * Constructor with status and message.
     *
     * @param status  the response status
     * @param message the response message
     */
    public InputDataResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Creates a successful response.
     *
     * @return successful response
     */
    public static InputDataResponse success() {
        return new InputDataResponse("success", "Input data saved successfully.");
    }

    /**
     * Creates a successful response with custom message.
     *
     * @param message the success message
     * @return successful response
     */
    public static InputDataResponse success(String message) {
        return new InputDataResponse("success", message);
    }

    /**
     * Creates an error response.
     *
     * @param message the error message
     * @return error response
     */
    public static InputDataResponse error(String message) {
        return new InputDataResponse("error", message);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "InputDataResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
