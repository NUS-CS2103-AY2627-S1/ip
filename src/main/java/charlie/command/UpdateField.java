package charlie.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import charlie.exception.CharlieException;

/**
 * Identifies a task field that can be changed by an update command.
 */
public enum UpdateField {
    /** Description of a task. */
    DESCRIPTION("description"),
    /** Due date of a deadline. */
    DEADLINE("deadline"),
    /** Start date and time of an event. */
    FROM("from"),
    /** End date and time of an event. */
    TO("to");

    /** Keyword that identifies this update field in user input. */
    private final String keyword;

    /**
     * Creates an update field associated with its user-facing keyword.
     *
     * @param keyword Keyword that identifies the field.
     */
    UpdateField(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Converts an update-field keyword into its corresponding enum value.
     *
     * @param keyword Update-field keyword entered by the user.
     * @return Matching update field.
     * @throws CharlieException If the keyword does not represent a supported field.
     */
    public static UpdateField parseKeyword(String keyword) {
        for (UpdateField updateField : values()) {
            if (updateField.keyword.equals(keyword)) {
                return updateField;
            }
        }
        throw new CharlieException(
                "Supported update fields are description, deadline, from, and to.");
    }

    /**
     * Verifies that a replacement value has the format required by this field.
     *
     * @param value Replacement value to validate.
     * @throws CharlieException If the value is empty or has an invalid format.
     */
    public void validateNewValue(String value) {
        if (value.isBlank()) {
            throw new CharlieException(
                    "The updated value cannot be empty.");
        }

        switch (this) {
            case DESCRIPTION -> { }
            case DEADLINE -> validateDeadline(value);
            case TO, FROM -> validateDateTime(value);
            default -> throw new AssertionError("Every update field must define validation");
        }
    }

    /**
     * Verifies that a replacement deadline uses the required date format.
     *
     * @param value Replacement deadline to validate.
     * @throws CharlieException If the value is not a valid date.
     */
    private static void validateDeadline(String value) {
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Deadline must use the yyyy-MM-dd format.");
        }
    }

    /**
     * Verifies that a replacement event time uses the required date-time format.
     *
     * @param value Replacement event time to validate.
     * @throws CharlieException If the value is not a valid date-time.
     */
    private static void validateDateTime(String value) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd HHmm")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDateTime.parse(value, formatter);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Event dates must use the yyyy-MM-dd HHmm format.");
        }
    }
}
