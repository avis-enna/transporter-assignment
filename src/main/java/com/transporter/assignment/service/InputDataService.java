package com.transporter.assignment.service;

import com.transporter.assignment.dto.InputDataRequest;
import com.transporter.assignment.dto.InputDataResponse;

/**
 * Service for managing input data stuff
 */
public interface InputDataService {

    /**
     * Save lanes and transporters data
     */
    InputDataResponse saveInputData(InputDataRequest request) throws InputDataException;

    /**
     * Remove all data
     */
    InputDataResponse clearInputData();

    /**
     * Validates the input data without saving it.
     *
     * @param request the input data request
     * @return the validation response
     */
    InputDataResponse validateInputData(InputDataRequest request);

    /**
     * Checks if input data exists in the system.
     *
     * @return true if input data exists, false otherwise
     */
    boolean hasInputData();

    /**
     * Gets statistics about the current input data.
     *
     * @return statistics about lanes, transporters, and quotes
     */
    InputDataStatistics getInputDataStatistics();
}
