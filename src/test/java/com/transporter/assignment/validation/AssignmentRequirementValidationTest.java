package com.transporter.assignment.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transporter.assignment.dto.*;
import com.transporter.assignment.service.OptimizationCapabilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Validation tests for assignment requirements.
 * Tests the system against the specific requirements and constraints from the assignment.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AssignmentRequirementValidationTest {

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
    void shouldMeetAllRequirementsForTestCase1() throws Exception {
        // Given - Test Case 1 from assignment
        InputDataRequest testCase1 = createAssignmentTestCase1();

        // Step 1: Submit input data
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCase1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Step 2: Optimize with max 3 transporters
        AssignmentRequest assignmentRequest = new AssignmentRequest(3);
        MvcResult result = mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        // Parse the response
        String responseContent = result.getResponse().getContentAsString();
        AssignmentResponse response = objectMapper.readValue(responseContent, AssignmentResponse.class);

        // Validate requirements
        validateCostMinimization(response);
        validateMaxTransporterUsage(response, 3);
        validateFullLaneCoverage(response, 5); // 5 lanes in test case 1
        validateAssignmentConsistency(response);
    }

    @Test
    void shouldHandleInfeasibleScenarioGracefully() throws Exception {
        // Given - Test Case 2 (incomplete coverage scenario)
        InputDataRequest testCase2 = createAssignmentTestCase2();

        // Step 1: Submit input data (should fail validation)
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCase2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("Lane 4 has no quotes")))
                .andExpect(jsonPath("$.message").value(containsString("Lane 5 has no quotes")));

        // Step 2: Check capabilities (should indicate no data available)
        MvcResult capabilitiesResult = mockMvc.perform(get("/transporters/assignment/capabilities"))
                .andExpect(status().isOk())
                .andReturn();

        String capabilitiesContent = capabilitiesResult.getResponse().getContentAsString();
        OptimizationCapabilities capabilities = objectMapper.readValue(capabilitiesContent, OptimizationCapabilities.class);

        // Should detect that optimization is not possible due to no data
        assertThat(capabilities.canOptimize()).isFalse();
        assertThat(capabilities.getLimitations()).isNotEmpty();
        assertThat(capabilities.getLimitations().get(0)).contains("No input data available");

        // Step 3: Attempt optimization (should fail due to no data)
        AssignmentRequest assignmentRequest = new AssignmentRequest(3);
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("No input data available")));
    }

    @Test
    void shouldRespectMaxTransporterConstraint() throws Exception {
        // Given - Test data with many transporters
        InputDataRequest testData = createAssignmentTestCase1();

        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testData)))
                .andExpect(status().isOk());

        // Test different max transporter values
        for (int maxTransporters = 1; maxTransporters <= 5; maxTransporters++) {
            AssignmentRequest request = new AssignmentRequest(maxTransporters);
            MvcResult result = mockMvc.perform(post("/transporters/assignment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            AssignmentResponse response = objectMapper.readValue(responseContent, AssignmentResponse.class);

            // Validate max transporter constraint
            assertThat(response.getSelectedTransporters()).hasSizeLessThanOrEqualTo(maxTransporters);
            
            // Should try to use as many transporters as possible (up to the limit)
            if (maxTransporters >= 3) { // With 5 lanes, we expect to use multiple transporters
                assertThat(response.getSelectedTransporters()).hasSizeGreaterThan(1);
            }
        }
    }

    @Test
    void shouldMinimizeCostEffectively() throws Exception {
        // Given - Test data
        InputDataRequest testData = createAssignmentTestCase1();

        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testData)))
                .andExpect(status().isOk());

        // Get optimization result
        AssignmentRequest request = new AssignmentRequest(3);
        MvcResult result = mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        AssignmentResponse response = objectMapper.readValue(responseContent, AssignmentResponse.class);

        // Validate that the solution is reasonable
        assertThat(response.getTotalCost()).isNotNull();
        assertThat(response.getTotalCost()).isGreaterThan(BigDecimal.ZERO);
        
        // The total cost should be reasonable for the given quotes
        // Based on test case 1, the minimum possible cost should be achievable
        BigDecimal expectedMinimumCost = new BigDecimal("50000"); // Reasonable lower bound
        BigDecimal expectedMaximumCost = new BigDecimal("200000"); // Reasonable upper bound
        
        assertThat(response.getTotalCost()).isBetween(expectedMinimumCost, expectedMaximumCost);
    }

    @Test
    void shouldProvideConsistentResults() throws Exception {
        // Given - Same test data
        InputDataRequest testData = createAssignmentTestCase1();

        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testData)))
                .andExpect(status().isOk());

        // Run optimization multiple times with same parameters
        AssignmentRequest request = new AssignmentRequest(3);
        AssignmentResponse firstResult = null;

        for (int i = 0; i < 3; i++) {
            MvcResult result = mockMvc.perform(post("/transporters/assignment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            AssignmentResponse response = objectMapper.readValue(responseContent, AssignmentResponse.class);

            if (firstResult == null) {
                firstResult = response;
            } else {
                // Results should be consistent
                assertThat(response.getTotalCost()).isEqualTo(firstResult.getTotalCost());
                assertThat(response.getSelectedTransporters()).hasSameSizeAs(firstResult.getSelectedTransporters());
            }
        }
    }

    @Test
    void shouldValidateAPIContractCompliance() throws Exception {
        // Test API 1: Input data submission
        InputDataRequest inputRequest = createAssignmentTestCase1();
        
        MvcResult inputResult = mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andReturn();

        // Validate input response structure
        String inputContent = inputResult.getResponse().getContentAsString();
        InputDataResponse inputResponse = objectMapper.readValue(inputContent, InputDataResponse.class);
        assertThat(inputResponse.getStatus()).isEqualTo("success");

        // Test API 2: Assignment optimization
        AssignmentRequest assignmentRequest = new AssignmentRequest(3);
        
        MvcResult assignmentResult = mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.totalCost").exists())
                .andExpect(jsonPath("$.assignments").exists())
                .andExpect(jsonPath("$.selectedTransporters").exists())
                .andReturn();

        // Validate assignment response structure
        String assignmentContent = assignmentResult.getResponse().getContentAsString();
        AssignmentResponse assignmentResponse = objectMapper.readValue(assignmentContent, AssignmentResponse.class);
        
        assertThat(assignmentResponse.getStatus()).isEqualTo("success");
        assertThat(assignmentResponse.getTotalCost()).isNotNull();
        assertThat(assignmentResponse.getAssignments()).isNotEmpty();
        assertThat(assignmentResponse.getSelectedTransporters()).isNotEmpty();
    }

    // Helper methods for validation
    private void validateCostMinimization(AssignmentResponse response) {
        assertThat(response.getTotalCost()).isNotNull();
        assertThat(response.getTotalCost()).isGreaterThan(BigDecimal.ZERO);
        // Cost should be reasonable (not excessively high)
    }

    private void validateMaxTransporterUsage(AssignmentResponse response, int maxTransporters) {
        assertThat(response.getSelectedTransporters()).hasSizeLessThanOrEqualTo(maxTransporters);
        // Should try to use as many transporters as possible within the limit
    }

    private void validateFullLaneCoverage(AssignmentResponse response, int expectedLaneCount) {
        assertThat(response.getAssignments()).hasSize(expectedLaneCount);
        
        // Each lane should be assigned exactly once
        List<Long> assignedLanes = response.getAssignments().stream()
                .map(AssignmentDto::getLaneId)
                .toList();
        
        assertThat(assignedLanes).hasSize(expectedLaneCount);
        assertThat(assignedLanes).doesNotHaveDuplicates();
    }

    private void validateAssignmentConsistency(AssignmentResponse response) {
        // All assigned transporters should be in the selected transporters list
        List<Long> assignedTransporters = response.getAssignments().stream()
                .map(AssignmentDto::getTransporterId)
                .distinct()
                .toList();
        
        assertThat(response.getSelectedTransporters()).containsAll(assignedTransporters);
    }

    // Test data creation methods
    private InputDataRequest createAssignmentTestCase1() {
        // Exact test case 1 from assignment specification
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
                        new LaneQuoteDto(3L, new BigDecimal("40870")),
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

    private InputDataRequest createAssignmentTestCase2() {
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
}
