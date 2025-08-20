package com.transporter.assignment.controller;

import com.transporter.assignment.dto.InputDataRequest;
import com.transporter.assignment.dto.InputDataResponse;
import com.transporter.assignment.service.InputDataException;
import com.transporter.assignment.service.InputDataService;
import com.transporter.assignment.service.InputDataStatistics;
import com.transporter.assignment.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing input data (lanes and transporters).
 */
@RestController
@RequestMapping("/transporters")
@Tag(name = "Input Data Management", description = "APIs for managing lanes and transporter data")
public class InputDataController {

    private final InputDataService inputDataService;

    @Autowired
    public InputDataController(InputDataService inputDataService) {
        this.inputDataService = inputDataService;
    }

    /**
     * Submit input data (lanes and transporters with quotes).
     */
    @PostMapping("/input")
    @Operation(summary = "Submit input data", description = "Submit lanes and transporters with quotes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InputDataResponse> submitInputData(
            @Valid @RequestBody InputDataRequest request) throws InputDataException {
        
        InputDataResponse response = inputDataService.saveInputData(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate input data without saving.
     */
    @PostMapping("/input/validate")
    @Operation(summary = "Validate input data", description = "Check data without saving")
    public ResponseEntity<InputDataResponse> validateInputData(@Valid @RequestBody InputDataRequest request) {
        InputDataResponse response = inputDataService.validateInputData(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Clear all existing input data.
     */
    @DeleteMapping("/input")
    @Operation(summary = "Clear input data", description = "Removes all data from the system")
    public ResponseEntity<InputDataResponse> clearInputData() {
        InputDataResponse response = inputDataService.clearInputData();
        return ResponseEntity.ok(response);
    }

    /**
     * Get some basic stats about the input data.
     */
    @GetMapping("/input/statistics")  
    @Operation(summary = "Get input data statistics")
    public ResponseEntity<InputDataStatistics> getInputDataStatistics() {
        InputDataStatistics statistics = inputDataService.getInputDataStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/input/exists")
    public ResponseEntity<Boolean> hasInputData() {
        boolean hasData = inputDataService.hasInputData();
        return ResponseEntity.ok(hasData);
    }

    @GetMapping("/input/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Input data service is healthy");
    }
}
