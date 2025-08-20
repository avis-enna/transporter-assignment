package com.transporter.assignment.algorithm;

import java.util.List;
import java.util.Objects;

/**
 * Result of validation for optimization input data.
 * Contains information about whether the optimization is feasible and any issues found.
 */
public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;
    private final String summary;

    /**
     * Constructor for validation result.
     *
     * @param valid    whether the validation passed
     * @param errors   list of error messages
     * @param warnings list of warning messages
     * @param summary  summary of validation result
     */
    public ValidationResult(boolean valid, List<String> errors, List<String> warnings, String summary) {
        this.valid = valid;
        this.errors = errors != null ? List.copyOf(errors) : List.of();
        this.warnings = warnings != null ? List.copyOf(warnings) : List.of();
        this.summary = summary;
    }

    /**
     * Creates a successful validation result.
     *
     * @return valid result with no errors
     */
    public static ValidationResult valid() {
        return new ValidationResult(true, List.of(), List.of(), "Validation passed");
    }

    /**
     * Creates a successful validation result with warnings.
     *
     * @param warnings list of warning messages
     * @return valid result with warnings
     */
    public static ValidationResult validWithWarnings(List<String> warnings) {
        return new ValidationResult(true, List.of(), warnings, "Validation passed with warnings");
    }

    /**
     * Creates an invalid validation result.
     *
     * @param errors list of error messages
     * @return invalid result with errors
     */
    public static ValidationResult invalid(List<String> errors) {
        return new ValidationResult(false, errors, List.of(), "Validation failed");
    }

    /**
     * Creates an invalid validation result with warnings.
     *
     * @param errors   list of error messages
     * @param warnings list of warning messages
     * @return invalid result with errors and warnings
     */
    public static ValidationResult invalid(List<String> errors, List<String> warnings) {
        return new ValidationResult(false, errors, warnings, "Validation failed");
    }

    /**
     * Checks if validation passed.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the list of errors.
     *
     * @return the errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Gets the list of warnings.
     *
     * @return the warnings
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Gets the validation summary.
     *
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Checks if there are any errors.
     *
     * @return true if errors exist
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Checks if there are any warnings.
     *
     * @return true if warnings exist
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return valid == that.valid &&
                Objects.equals(errors, that.errors) &&
                Objects.equals(warnings, that.warnings) &&
                Objects.equals(summary, that.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, errors, warnings, summary);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errors=" + errors.size() +
                ", warnings=" + warnings.size() +
                ", summary='" + summary + '\'' +
                '}';
    }
}
