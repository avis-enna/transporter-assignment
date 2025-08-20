package com.transporter.assignment.controller;

import com.transporter.assignment.dto.AssignmentRequest;
import com.transporter.assignment.dto.AssignmentResponse;
import com.transporter.assignment.service.AssignmentException;
import com.transporter.assignment.service.AssignmentService;
import com.transporter.assignment.service.OptimizationCapabilities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for transporter assignment optimization.
 * Provides endpoints for optimizing transporter assignments to lanes.
 */
@RestController
@RequestMapping("/transporters")
@Tag(name = "Assignment Optimization", description = "APIs for optimizing transporter assignments to lanes")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * Optimizes transporter assignment to lanes.
     *
     * @param request the assignment request with optimization parameters
     * @return optimized assignment result
     * @throws AssignmentException if optimization fails
     */
    @PostMapping("/assignment")
    @Operation(
            summary = "Optimize transporter assignment",
            description = "Optimizes the assignment of transporters to lanes based on the specified parameters. " +
                         "The optimization considers cost minimization, transporter usage maximization, and full lane coverage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment optimization completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or optimization not possible"),
            @ApiResponse(responseCode = "500", description = "Internal server error during optimization")
    })
    public ResponseEntity<AssignmentResponse> optimizeAssignment(
            @Parameter(description = "Assignment optimization parameters", required = true)
            @Valid @RequestBody AssignmentRequest request) throws AssignmentException {
        
        AssignmentResponse response = assignmentService.optimizeAssignment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validates assignment parameters without running optimization.
     *
     * @param request the assignment request to validate
     * @return validation response
     */
    @PostMapping("/assignment/validate")
    @Operation(
            summary = "Validate assignment parameters",
            description = "Validates the assignment optimization parameters and checks if optimization is possible " +
                         "with the current input data without actually running the optimization."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation completed"),
            @ApiResponse(responseCode = "400", description = "Validation errors or optimization not possible")
    })
    public ResponseEntity<AssignmentResponse> validateAssignment(
            @Parameter(description = "Assignment parameters to validate", required = true)
            @Valid @RequestBody AssignmentRequest request) {
        
        AssignmentResponse response = assignmentService.validateAssignment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets optimization capabilities based on current input data.
     *
     * @return optimization capabilities and constraints
     */
    @GetMapping("/assignment/capabilities")
    @Operation(
            summary = "Get optimization capabilities",
            description = "Returns information about what optimization is possible with the current input data, " +
                         "including constraints, limitations, and recommendations."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capabilities retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Error analyzing capabilities")
    })
    public ResponseEntity<OptimizationCapabilities> getOptimizationCapabilities() {
        OptimizationCapabilities capabilities = assignmentService.getOptimizationCapabilities();
        return ResponseEntity.ok(capabilities);
    }

    /**
     * Quick optimization with default parameters.
     *
     * @param maxTransporters maximum number of transporters to use
     * @return optimized assignment result
     * @throws AssignmentException if optimization fails
     */
    @PostMapping("/assignment/quick")
    @Operation(
            summary = "Quick optimization with default parameters",
            description = "Performs optimization with default parameters (cost minimization and full coverage enabled). " +
                         "Only requires specifying the maximum number of transporters."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quick optimization completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters or optimization not possible"),
            @ApiResponse(responseCode = "500", description = "Internal server error during optimization")
    })
    public ResponseEntity<AssignmentResponse> quickOptimization(
            @Parameter(description = "Maximum number of transporters to use", required = true)
            @RequestParam int maxTransporters) throws AssignmentException {

        if (maxTransporters <= 0) {
            throw new IllegalArgumentException("Max transporters must be positive");
        }
        
        AssignmentRequest request = new AssignmentRequest(maxTransporters);
        AssignmentResponse response = assignmentService.optimizeAssignment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for the assignment service.
     *
     * @return health status
     */
    @GetMapping("/assignment/health")
    @Operation(
            summary = "Assignment service health check",
            description = "Returns the health status of the assignment optimization service."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Assignment service is healthy");
    }
}
