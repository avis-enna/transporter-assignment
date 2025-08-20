package com.transporter.assignment.algorithm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for OptimizationParameters.
 */
class OptimizationParametersTest {

    @Test
    void shouldCreateParametersWithAllFields() {
        // When
        OptimizationParameters params = new OptimizationParameters(5, true, true, true, 30);

        // Then
        assertThat(params.getMaxTransporters()).isEqualTo(5);
        assertThat(params.isMinimizeCost()).isTrue();
        assertThat(params.isMaximizeTransporterUsage()).isTrue();
        assertThat(params.isEnsureFullCoverage()).isTrue();
        assertThat(params.getTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldCreateDefaultParameters() {
        // When
        OptimizationParameters params = OptimizationParameters.defaultParameters(3);

        // Then
        assertThat(params.getMaxTransporters()).isEqualTo(3);
        assertThat(params.isMinimizeCost()).isTrue();
        assertThat(params.isMaximizeTransporterUsage()).isTrue();
        assertThat(params.isEnsureFullCoverage()).isTrue();
        assertThat(params.getTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldCreateCostMinimizationParameters() {
        // When
        OptimizationParameters params = OptimizationParameters.costMinimization(4);

        // Then
        assertThat(params.getMaxTransporters()).isEqualTo(4);
        assertThat(params.isMinimizeCost()).isTrue();
        assertThat(params.isMaximizeTransporterUsage()).isFalse();
        assertThat(params.isEnsureFullCoverage()).isTrue();
        assertThat(params.getTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldCreateMaxTransporterUsageParameters() {
        // When
        OptimizationParameters params = OptimizationParameters.maxTransporterUsage(6);

        // Then
        assertThat(params.getMaxTransporters()).isEqualTo(6);
        assertThat(params.isMinimizeCost()).isFalse();
        assertThat(params.isMaximizeTransporterUsage()).isTrue();
        assertThat(params.isEnsureFullCoverage()).isTrue();
        assertThat(params.getTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldThrowExceptionForInvalidMaxTransporters() {
        // When/Then
        assertThatThrownBy(() -> new OptimizationParameters(0, true, true, true, 30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Max transporters must be positive");

        assertThatThrownBy(() -> new OptimizationParameters(-1, true, true, true, 30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Max transporters must be positive");
    }

    @Test
    void shouldThrowExceptionForInvalidTimeout() {
        // When/Then
        assertThatThrownBy(() -> new OptimizationParameters(3, true, true, true, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Timeout must be positive");

        assertThatThrownBy(() -> new OptimizationParameters(3, true, true, true, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Timeout must be positive");
    }

    @Test
    void shouldCreateCopyWithDifferentMaxTransporters() {
        // Given
        OptimizationParameters original = OptimizationParameters.defaultParameters(3);

        // When
        OptimizationParameters modified = original.withMaxTransporters(5);

        // Then
        assertThat(modified.getMaxTransporters()).isEqualTo(5);
        assertThat(modified.isMinimizeCost()).isEqualTo(original.isMinimizeCost());
        assertThat(modified.isMaximizeTransporterUsage()).isEqualTo(original.isMaximizeTransporterUsage());
        assertThat(modified.isEnsureFullCoverage()).isEqualTo(original.isEnsureFullCoverage());
        assertThat(modified.getTimeoutSeconds()).isEqualTo(original.getTimeoutSeconds());

        // Original should be unchanged
        assertThat(original.getMaxTransporters()).isEqualTo(3);
    }

    @Test
    void shouldCreateCopyWithDifferentTimeout() {
        // Given
        OptimizationParameters original = OptimizationParameters.defaultParameters(3);

        // When
        OptimizationParameters modified = original.withTimeout(60);

        // Then
        assertThat(modified.getTimeoutSeconds()).isEqualTo(60);
        assertThat(modified.getMaxTransporters()).isEqualTo(original.getMaxTransporters());
        assertThat(modified.isMinimizeCost()).isEqualTo(original.isMinimizeCost());
        assertThat(modified.isMaximizeTransporterUsage()).isEqualTo(original.isMaximizeTransporterUsage());
        assertThat(modified.isEnsureFullCoverage()).isEqualTo(original.isEnsureFullCoverage());

        // Original should be unchanged
        assertThat(original.getTimeoutSeconds()).isEqualTo(30);
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        OptimizationParameters params1 = new OptimizationParameters(3, true, true, true, 30);
        OptimizationParameters params2 = new OptimizationParameters(3, true, true, true, 30);
        OptimizationParameters params3 = new OptimizationParameters(4, true, true, true, 30);

        // Then
        assertThat(params1).isEqualTo(params2);
        assertThat(params1).isNotEqualTo(params3);
        assertThat(params1.hashCode()).isEqualTo(params2.hashCode());
        assertThat(params1.hashCode()).isNotEqualTo(params3.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        // Given
        OptimizationParameters params = new OptimizationParameters(3, true, false, true, 45);

        // When
        String toString = params.toString();

        // Then
        assertThat(toString).contains("maxTransporters=3");
        assertThat(toString).contains("minimizeCost=true");
        assertThat(toString).contains("maximizeTransporterUsage=false");
        assertThat(toString).contains("ensureFullCoverage=true");
        assertThat(toString).contains("timeoutSeconds=45");
    }

    @Test
    void shouldHandleDifferentParameterCombinations() {
        // Test various combinations
        OptimizationParameters params1 = new OptimizationParameters(1, false, false, false, 1);
        OptimizationParameters params2 = new OptimizationParameters(100, true, true, true, 3600);

        assertThat(params1.getMaxTransporters()).isEqualTo(1);
        assertThat(params1.isMinimizeCost()).isFalse();
        assertThat(params1.isMaximizeTransporterUsage()).isFalse();
        assertThat(params1.isEnsureFullCoverage()).isFalse();
        assertThat(params1.getTimeoutSeconds()).isEqualTo(1);

        assertThat(params2.getMaxTransporters()).isEqualTo(100);
        assertThat(params2.isMinimizeCost()).isTrue();
        assertThat(params2.isMaximizeTransporterUsage()).isTrue();
        assertThat(params2.isEnsureFullCoverage()).isTrue();
        assertThat(params2.getTimeoutSeconds()).isEqualTo(3600);
    }
}
