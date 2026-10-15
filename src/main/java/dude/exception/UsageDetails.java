package dude.exception;

import java.util.Objects;

/**
 * Groups the structured information needed to explain invalid user input.
 *
 * @param action       Command that received invalid input.
 * @param fieldName    Invalid command field.
 * @param actualValue  Supplied value.
 * @param expectedType Expected value type or format.
 * @param usageMessage Valid command usage.
 * @param usageToken   Usage token to highlight.
 */
public record UsageDetails(String action, String fieldName, String actualValue,
        String expectedType, String usageMessage, String usageToken) {
    /**
     * Validates that structured usage details contain the fields needed for display.
     *
     * @param action       Command that received invalid input.
     * @param fieldName    Invalid command field.
     * @param actualValue  Supplied value.
     * @param expectedType Expected value type or format.
     * @param usageMessage Valid command usage.
     * @param usageToken   Usage token to highlight.
     */
    public UsageDetails {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(fieldName, "fieldName");
        Objects.requireNonNull(actualValue, "actualValue");
        Objects.requireNonNull(expectedType, "expectedType");
        Objects.requireNonNull(usageMessage, "usageMessage");
        Objects.requireNonNull(usageToken, "usageToken");
    }
}
