package com.transporter.assignment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transporter.assignment.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the complete transporter assignment system.
 * Tests the full flow from input data submission to optimization results.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransporterAssignmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // Clear any existing data before each test
        mockMvc.perform(delete("/transporters/input"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCompleteFullWorkflowSuccessfully() throws Exception {
        // Given - Test Case 1 from assignment
        InputDataRequest inputRequest = createTestCase1Data();

        // Step 1: Submit input data
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Step 2: Verify data was saved
        mockMvc.perform(get("/transporters/input/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Step 3: Get statistics
        mockMvc.perform(get("/transporters/input/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.laneCount").value(5))
                .andExpect(jsonPath("$.transporterCount").value(7))
                .andExpect(jsonPath("$.quoteCount").value(35)); // 7 transporters * 5 lanes each

        // Step 4: Check optimization capabilities
        mockMvc.perform(get("/transporters/assignment/capabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canOptimize").value(true))
                .andExpect(jsonPath("$.maxPossibleTransporters").value(7))
                .andExpect(jsonPath("$.minRequiredTransporters").value(1));

        // Step 5: Validate assignment parameters
        AssignmentRequest assignmentRequest = new AssignmentRequest(3);
        mockMvc.perform(post("/transporters/assignment/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Step 6: Perform optimization
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.totalCost").exists())
                .andExpect(jsonPath("$.assignments").isArray())
                .andExpect(jsonPath("$.assignments").value(hasSize(5))) // 5 lanes
                .andExpect(jsonPath("$.selectedTransporters").isArray())
                .andExpect(jsonPath("$.selectedTransporters").value(hasSize(lessThanOrEqualTo(3))));

        // Step 7: Test quick optimization
        mockMvc.perform(post("/transporters/assignment/quick")
                        .param("maxTransporters", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.selectedTransporters").value(hasSize(lessThanOrEqualTo(2))));
    }

    @Test
    void shouldHandleTestCase2Scenario() throws Exception {
        // Given - Test Case 2 (incomplete coverage scenario)
        InputDataRequest inputRequest = createTestCase2Data();

        // Step 1: Submit input data (should fail validation due to uncovered lanes)
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("Lane 4 has no quotes")))
                .andExpect(jsonPath("$.message").value(containsString("Lane 5 has no quotes")));

        // Step 2: Verify that no data was saved due to validation failure
        mockMvc.perform(get("/transporters/input/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        // Step 3: Check optimization capabilities (should indicate no data)
        mockMvc.perform(get("/transporters/assignment/capabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canOptimize").value(false))
                .andExpect(jsonPath("$.limitations[0]").value(containsString("No input data available")));
    }

    @Test
    void shouldValidateInputDataCorrectly() throws Exception {
        // Given - Invalid input data with duplicate lane IDs
        List<LaneDto> invalidLanes = List.of(
                new LaneDto(1L, "Mumbai", "Delhi"),
                new LaneDto(1L, "Chennai", "Bangalore") // Duplicate ID
        );
        List<TransporterDto> validTransporters = List.of(
                new TransporterDto(1L, "T1", List.of(new LaneQuoteDto(1L, new BigDecimal("5000"))))
        );
        InputDataRequest invalidRequest = new InputDataRequest(invalidLanes, validTransporters);

        // When & Then
        mockMvc.perform(post("/transporters/input/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("Duplicate lane ID")));
    }

    @Test
    void shouldHandleEmptyDataScenarios() throws Exception {
        // Test optimization without any input data
        AssignmentRequest assignmentRequest = new AssignmentRequest(3);
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("No input data available")));

        // Test capabilities without data
        mockMvc.perform(get("/transporters/assignment/capabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canOptimize").value(false));

        // Test statistics without data
        mockMvc.perform(get("/transporters/input/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.laneCount").value(0))
                .andExpect(jsonPath("$.transporterCount").value(0));
    }

    @Test
    void shouldHandleDataClearingCorrectly() throws Exception {
        // Given - Submit some data first
        InputDataRequest inputRequest = createSimpleTestData();
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRequest)))
                .andExpect(status().isOk());

        // Verify data exists
        mockMvc.perform(get("/transporters/input/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Clear data
        mockMvc.perform(delete("/transporters/input"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Verify data is cleared
        mockMvc.perform(get("/transporters/input/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void shouldHandleHealthChecks() throws Exception {
        // Test input service health
        mockMvc.perform(get("/transporters/input/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Input data service is healthy"));

        // Test assignment service health
        mockMvc.perform(get("/transporters/assignment/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Assignment service is healthy"));
    }

    // Helper methods for creating test data
    private InputDataRequest createTestCase1Data() {
        // Test Case 1 from assignment specification
        List<LaneDto> lanes = List.of(
                new LaneDto(1L, "Mumbai", "Delhi"),
                new LaneDto(2L, "Delhi", "Bangalore"),
                new LaneDto(3L, "Chennai", "Kolkata"),
                new LaneDto(4L, "Pune", "Hyderabad"),
                new LaneDto(5L, "Ahmedabad", "Jaipur")
        );

        List<TransporterDto> transporters = List.of(
                new TransporterDto(1L, "Transporter T1", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("20835")),
                        new LaneQuoteDto(2L, new BigDecimal("10512")),
                        new LaneQuoteDto(3L, new BigDecimal("22105")),
                        new LaneQuoteDto(4L, new BigDecimal("42481")),
                        new LaneQuoteDto(5L, new BigDecimal("19862"))
                )),
                new TransporterDto(2L, "Transporter T2", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("48844")),
                        new LaneQuoteDto(2L, new BigDecimal("31326")),
                        new LaneQuoteDto(3L, new BigDecimal("18640")),
                        new LaneQuoteDto(4L, new BigDecimal("45828")),
                        new LaneQuoteDto(5L, new BigDecimal("18297"))
                )),
                new TransporterDto(3L, "Transporter T3", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("39020")),
                        new LaneQuoteDto(2L, new BigDecimal("20648")),
                        new LaneQuoteDto(3L, new BigDecimal("31438")),
                        new LaneQuoteDto(4L, new BigDecimal("36447")),
                        new LaneQuoteDto(5L, new BigDecimal("12789"))
                )),
                new TransporterDto(4L, "Transporter T4", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("14400")),
                        new LaneQuoteDto(2L, new BigDecimal("44514")),
                        new LaneQuoteDto(3L, new BigDecimal("14316")),
                        new LaneQuoteDto(4L, new BigDecimal("10678")),
                        new LaneQuoteDto(5L, new BigDecimal("13032"))
                )),
                new TransporterDto(5L, "Transporter T5", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("11601")),
                        new LaneQuoteDto(2L, new BigDecimal("19760")),
                        new LaneQuoteDto(3L, new BigDecimal("14316")),
                        new LaneQuoteDto(4L, new BigDecimal("20635")),
                        new LaneQuoteDto(5L, new BigDecimal("26421"))
                )),
                new TransporterDto(6L, "Transporter T6", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("35095")),
                        new LaneQuoteDto(2L, new BigDecimal("12494")),
                        new LaneQuoteDto(3L, new BigDecimal("17808")),
                        new LaneQuoteDto(4L, new BigDecimal("36210")),
                        new LaneQuoteDto(5L, new BigDecimal("39444"))
                )),
                new TransporterDto(7L, "Transporter T7", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("26070")),
                        new LaneQuoteDto(2L, new BigDecimal("41098")),
                        new LaneQuoteDto(3L, new BigDecimal("20932")),
                        new LaneQuoteDto(4L, new BigDecimal("16897")),
                        new LaneQuoteDto(5L, new BigDecimal("27938"))
                ))
        );

        return new InputDataRequest(lanes, transporters);
    }

    private InputDataRequest createTestCase2Data() {
        // Test Case 2 - truly infeasible scenario with uncovered lanes
        List<LaneDto> lanes = List.of(
                new LaneDto(1L, "Chandigarh", "Shimla"),
                new LaneDto(2L, "Agra", "Kanpur"),
                new LaneDto(3L, "Varanasi", "Gorakhpur"),
                new LaneDto(4L, "Amritsar", "Ludhiana"),
                new LaneDto(5L, "Coimbatore", "Madurai")
        );

        // Only provide quotes for lanes 1, 2, and 3 - leaving lanes 4 and 5 uncovered
        List<TransporterDto> transporters = List.of(
                new TransporterDto(1L, "Transporter X", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("1000")),
                        new LaneQuoteDto(2L, new BigDecimal("1500"))
                )),
                new TransporterDto(2L, "Transporter Y", List.of(
                        new LaneQuoteDto(2L, new BigDecimal("1600")),
                        new LaneQuoteDto(3L, new BigDecimal("2000"))
                )),
                new TransporterDto(3L, "Transporter Z", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("1200")),
                        new LaneQuoteDto(3L, new BigDecimal("1800"))
                ))
        );

        return new InputDataRequest(lanes, transporters);
    }

    private InputDataRequest createSimpleTestData() {
        List<LaneDto> lanes = List.of(
                new LaneDto(1L, "Mumbai", "Delhi"),
                new LaneDto(2L, "Chennai", "Bangalore")
        );

        List<TransporterDto> transporters = List.of(
                new TransporterDto(1L, "T1", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("5000")),
                        new LaneQuoteDto(2L, new BigDecimal("7000"))
                )),
                new TransporterDto(2L, "T2", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("5500")),
                        new LaneQuoteDto(2L, new BigDecimal("6500"))
                ))
        );

        return new InputDataRequest(lanes, transporters);
    }
}
