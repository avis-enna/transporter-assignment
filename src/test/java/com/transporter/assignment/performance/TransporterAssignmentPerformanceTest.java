package com.transporter.assignment.performance;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Performance tests for the transporter assignment system.
 * Tests system behavior under various load conditions and data sizes.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransporterAssignmentPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Random random = new Random(42); // Fixed seed for reproducible tests

    @BeforeEach
    void setUp() throws Exception {
        // Clear any existing data before each test
        mockMvc.perform(delete("/transporters/input"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldHandleSmallDatasetEfficiently() throws Exception {
        // Given - Small dataset (5 lanes, 3 transporters)
        InputDataRequest smallDataset = generateTestData(5, 3, 0.8);

        // When & Then - Should complete quickly
        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(smallDataset)))
                .andExpect(status().isOk());

        AssignmentRequest assignmentRequest = new AssignmentRequest(2);
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Should complete within 1 second for small dataset
        assert duration < 1000 : "Small dataset optimization took too long: " + duration + "ms";
    }

    @Test
    void shouldHandleMediumDatasetReasonably() throws Exception {
        // Given - Medium dataset (20 lanes, 10 transporters)
        InputDataRequest mediumDataset = generateTestData(20, 10, 0.7);

        // When & Then - Should complete within reasonable time
        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mediumDataset)))
                .andExpect(status().isOk());

        AssignmentRequest assignmentRequest = new AssignmentRequest(5);
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Should complete within 5 seconds for medium dataset
        assert duration < 5000 : "Medium dataset optimization took too long: " + duration + "ms";
    }

    @Test
    void shouldHandleLargeDatasetWithTimeout() throws Exception {
        // Given - Large dataset (50 lanes, 20 transporters)
        InputDataRequest largeDataset = generateTestData(50, 20, 0.6);

        // When & Then - Should complete or timeout gracefully
        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(largeDataset)))
                .andExpect(status().isOk());

        AssignmentRequest assignmentRequest = new AssignmentRequest(10, true, true, true, 30); // 30 second timeout
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(anyOf(equalTo("success"), equalTo("failure")))); // Either succeeds or fails gracefully

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Should complete within 35 seconds (including timeout buffer)
        assert duration < 35000 : "Large dataset optimization took too long: " + duration + "ms";
    }

    @Test
    void shouldHandleMultipleQuickOptimizations() throws Exception {
        // Given - Setup data once
        InputDataRequest dataset = generateTestData(10, 5, 0.8);
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dataset)))
                .andExpect(status().isOk());

        // When & Then - Perform multiple quick optimizations
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= 5; i++) {
            mockMvc.perform(post("/transporters/assignment/quick")
                            .param("maxTransporters", String.valueOf(i)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"));
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Multiple optimizations should complete within 3 seconds
        assert duration < 3000 : "Multiple quick optimizations took too long: " + duration + "ms";
    }

    @Test
    void shouldHandleValidationQuickly() throws Exception {
        // Given - Various dataset sizes
        List<InputDataRequest> datasets = List.of(
                generateTestData(5, 3, 1.0),
                generateTestData(15, 8, 0.9),
                generateTestData(25, 12, 0.8)
        );

        for (InputDataRequest dataset : datasets) {
            // Clear previous data
            mockMvc.perform(delete("/transporters/input"))
                    .andExpect(status().isOk());

            long startTime = System.currentTimeMillis();

            // Submit and validate
            mockMvc.perform(post("/transporters/input/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dataset)))
                    .andExpect(status().isOk());

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Validation should be very fast
            assert duration < 500 : "Validation took too long: " + duration + "ms";
        }
    }

    @Test
    void shouldHandleStatisticsCalculationEfficiently() throws Exception {
        // Given - Large dataset for statistics
        InputDataRequest largeDataset = generateTestData(100, 30, 0.5);

        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(largeDataset)))
                .andExpect(status().isOk());

        // When & Then - Statistics calculation should be fast
        long startTime = System.currentTimeMillis();

        mockMvc.perform(get("/transporters/input/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.laneCount").value(100))
                .andExpect(jsonPath("$.transporterCount").value(30));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Statistics should be calculated quickly
        assert duration < 1000 : "Statistics calculation took too long: " + duration + "ms";
    }

    @Test
    void shouldHandleCapabilitiesAnalysisEfficiently() throws Exception {
        // Given - Complex dataset
        InputDataRequest complexDataset = generateTestData(30, 15, 0.6);

        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(complexDataset)))
                .andExpect(status().isOk());

        // When & Then - Capabilities analysis should be fast
        long startTime = System.currentTimeMillis();

        mockMvc.perform(get("/transporters/assignment/capabilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canOptimize").exists())
                .andExpect(jsonPath("$.maxPossibleTransporters").exists());

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Capabilities analysis should be quick
        assert duration < 2000 : "Capabilities analysis took too long: " + duration + "ms";
    }

    @Test
    void shouldHandleMemoryEfficientlyWithLargeDataset() throws Exception {
        // Given - Very large dataset to test memory usage
        InputDataRequest veryLargeDataset = generateTestData(200, 50, 0.4);

        // When & Then - Should handle large dataset without memory issues
        mockMvc.perform(post("/transporters/input")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veryLargeDataset)))
                .andExpect(status().isOk());

        // Verify data was saved correctly
        mockMvc.perform(get("/transporters/input/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.laneCount").value(200))
                .andExpect(jsonPath("$.transporterCount").value(50));

        // Test optimization with timeout to prevent hanging
        AssignmentRequest assignmentRequest = new AssignmentRequest(20, true, true, true, 10);
        mockMvc.perform(post("/transporters/assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(anyOf(equalTo("success"), equalTo("failure"))));
    }

    /**
     * Generates test data with specified parameters.
     *
     * @param laneCount number of lanes to generate
     * @param transporterCount number of transporters to generate
     * @param coverageRatio ratio of lanes each transporter should cover (0.0 to 1.0)
     * @return generated test data
     */
    private InputDataRequest generateTestData(int laneCount, int transporterCount, double coverageRatio) {
        List<LaneDto> lanes = new ArrayList<>();
        for (int i = 1; i <= laneCount; i++) {
            lanes.add(new LaneDto((long) i, "Origin" + i, "Destination" + i));
        }

        List<TransporterDto> transporters = new ArrayList<>();
        for (int t = 1; t <= transporterCount; t++) {
            List<LaneQuoteDto> quotes = new ArrayList<>();
            
            // Each transporter covers a percentage of lanes based on coverageRatio
            int lanesToCover = Math.max(1, (int) (laneCount * coverageRatio));
            
            // Randomly select lanes for this transporter
            List<Integer> selectedLanes = new ArrayList<>();
            for (int i = 1; i <= laneCount; i++) {
                if (random.nextDouble() < coverageRatio) {
                    selectedLanes.add(i);
                }
            }
            
            // Ensure minimum coverage
            while (selectedLanes.size() < Math.min(lanesToCover, laneCount)) {
                int randomLane = random.nextInt(laneCount) + 1;
                if (!selectedLanes.contains(randomLane)) {
                    selectedLanes.add(randomLane);
                }
            }
            
            // Generate quotes for selected lanes
            for (Integer laneId : selectedLanes) {
                // Generate random quote between 1000 and 50000
                int quote = 1000 + random.nextInt(49000);
                quotes.add(new LaneQuoteDto(laneId.longValue(), new BigDecimal(quote)));
            }
            
            transporters.add(new TransporterDto((long) t, "Transporter T" + t, quotes));
        }

        return new InputDataRequest(lanes, transporters);
    }
}
