package com.transporter.assignment.testutil;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base test class providing common setup and utilities for all tests.
 */
@ActiveProfiles("test")
public abstract class BaseTest {
    
    @BeforeEach
    void baseSetUp() {
        // Common setup for all tests
        setupTestData();
    }
    
    /**
     * Override this method in subclasses to provide specific test data setup.
     */
    protected void setupTestData() {
        // Default implementation - can be overridden
    }
    
    /**
     * Utility method to create test data using the builder pattern.
     */
    protected TestDataBuilder.Scenarios scenarios() {
        return new TestDataBuilder.Scenarios();
    }
    
    /**
     * Utility method to access lane builder.
     */
    protected TestDataBuilder.LaneBuilder lane() {
        return TestDataBuilder.lane();
    }
    
    /**
     * Utility method to access transporter builder.
     */
    protected TestDataBuilder.TransporterBuilder transporter() {
        return TestDataBuilder.transporter();
    }
    
    /**
     * Utility method to access assignment builder.
     */
    protected TestDataBuilder.AssignmentBuilder assignment() {
        return TestDataBuilder.assignment();
    }
    
    /**
     * Utility method to access lane quote builder.
     */
    protected TestDataBuilder.LaneQuoteBuilder laneQuote() {
        return TestDataBuilder.laneQuote();
    }
}
