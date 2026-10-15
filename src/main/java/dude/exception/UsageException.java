package dude.exception;

import java.util.Objects;

/**
 * Describes invalid command input and the format expected by the command.
 */
public final class UsageException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Structured information used to render this exception. */
    private final UsageDetails details;

    /**
     * Creates an exception describing invalid command input.
     *
     * @param details Structured details about the invalid input.
     */
    public UsageException(UsageDetails details) {
        this(details, null);
    }

    /**
     * Creates an exception describing invalid command input with its cause.
     *
     * @param details Structured details about the invalid input.
     * @param cause   Underlying parsing or validation failure.
     */
    public UsageException(UsageDetails details, Throwable cause) {
        super(formatMessage(Objects.requireNonNull(details)), cause);
        this.details = details;
    }

    /**
     * Returns the command that received invalid input.
     *
     * @return Invalid command action.
     */
    public String getAction() {
        return details.action();
    }

    /**
     * Returns the name of the invalid command field.
     *
     * @return Invalid field name.
     */
    public String getFieldName() {
        return details.fieldName();
    }

    /**
     * Returns the supplied value.
     *
     * @return Supplied value.
     */
    public String getActualValue() {
        return details.actualValue();
    }

    /**
     * Returns the expected type or format.
     *
     * @return Expected type or format.
     */
    public String getExpectedType() {
        return details.expectedType();
    }

    /**
     * Returns the command usage message.
     *
     * @return Usage message.
     */
    public String getUsageMessage() {
        return details.usageMessage();
    }

    /**
     * Returns the usage token that should be highlighted.
     *
     * @return Usage token.
     */
    public String getUsageToken() {
        return details.usageToken();
    }

    /**
     * Formats structured usage details into the exception's diagnostic message.
     *
     * @param details Structured usage details.
     * @return Human-readable diagnostic message.
     */
    private static String formatMessage(UsageDetails details) {
        return String.format("Invalid %s for %s: %s (expected %s)",
                details.fieldName(), details.action(), details.actualValue(), details.expectedType());
    }
}
