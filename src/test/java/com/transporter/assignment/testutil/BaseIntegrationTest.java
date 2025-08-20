package com.transporter.assignment.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Base integration test class providing common setup and utilities for integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class BaseIntegrationTest extends BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void clearData() throws Exception {
        // Clear any existing data before each test
        mockMvc.perform(delete("/transporters/input"))
                .andExpect(status().isOk());
    }

    /**
     * Helper method to submit input data.
     */
    protected ResultActions submitInputData(Object inputRequest) throws Exception {
        return mockMvc.perform(post("/transporters/input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest)));
    }

    /**
     * Helper method to validate input data.
     */
    protected ResultActions validateInputData(Object inputRequest) throws Exception {
        return mockMvc.perform(post("/transporters/input/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRequest)));
    }

    /**
     * Helper method to optimize assignment.
     */
    protected ResultActions optimizeAssignment(Object assignmentRequest) throws Exception {
        return mockMvc.perform(post("/transporters/assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentRequest)));
    }

    /**
     * Helper method to validate assignment.
     */
    protected ResultActions validateAssignment(Object assignmentRequest) throws Exception {
        return mockMvc.perform(post("/transporters/assignment/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentRequest)));
    }

    /**
     * Helper method to get optimization capabilities.
     */
    protected ResultActions getOptimizationCapabilities() throws Exception {
        return mockMvc.perform(get("/transporters/assignment/capabilities"));
    }

    /**
     * Helper method to get input data statistics.
     */
    protected ResultActions getInputDataStatistics() throws Exception {
        return mockMvc.perform(get("/transporters/input/statistics"));
    }

    /**
     * Helper method to check if input data exists.
     */
    protected ResultActions checkInputDataExists() throws Exception {
        return mockMvc.perform(get("/transporters/input/exists"));
    }

    /**
     * Helper method to perform quick optimization.
     */
    protected ResultActions quickOptimization(int maxTransporters) throws Exception {
        return mockMvc.perform(post("/transporters/assignment/quick")
                .param("maxTransporters", String.valueOf(maxTransporters)));
    }

    /**
     * Helper method to clear input data.
     */
    protected ResultActions clearInputData() throws Exception {
        return mockMvc.perform(delete("/transporters/input"));
    }

    /**
     * Helper method to check input service health.
     */
    protected ResultActions checkInputServiceHealth() throws Exception {
        return mockMvc.perform(get("/transporters/input/health"));
    }

    /**
     * Helper method to check assignment service health.
     */
    protected ResultActions checkAssignmentServiceHealth() throws Exception {
        return mockMvc.perform(get("/transporters/assignment/health"));
    }
}
