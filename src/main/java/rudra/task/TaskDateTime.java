package rudra.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;

import rudra.exception.RudraException;

/**
 * Represents a task date or date-time value in parsed form.
 */
public class TaskDateTime implements Comparable<TaskDateTime> {
    private static final Map<Long, String> AM_PM_DISPLAY_TEXT = Map.of(0L, "am", 1L, "pm");
    private static final DateTimeFormatter DATE_INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter DATE_TIME_INPUT_WITH_COLON_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("MMM d yyyy h:mm")
            .appendText(ChronoField.AMPM_OF_DAY, AM_PM_DISPLAY_TEXT)
            .toFormatter(Locale.ENGLISH);

    private final LocalDateTime value;
    private final boolean hasTime;

    /**
     * Creates a parsed task date or date-time.
     *
     * @param value Parsed date-time value.
     * @param hasTime Whether the original input included a time component.
     */
    public TaskDateTime(LocalDateTime value, boolean hasTime) {
        assert value != null : "A task date-time must have a value";
        assert hasTime || value.toLocalTime().equals(LocalTime.MIDNIGHT)
                : "A date without a time must be stored at midnight";

        this.value = value;
        this.hasTime = hasTime;
    }

    /**
     * Parses a user-entered task date or date-time.
     *
     * @param rawValue User input to parse.
     * @return Parsed task date or date-time.
     * @throws RudraException If the input does not match the accepted formats.
     */
    public static TaskDateTime parse(String rawValue) throws RudraException {
        try {
            if (rawValue.contains(":")) {
                return new TaskDateTime(LocalDateTime.parse(rawValue, DATE_TIME_INPUT_WITH_COLON_FORMATTER), true);
            }

            if (rawValue.contains(" ")) {
                return new TaskDateTime(LocalDateTime.parse(rawValue, DATE_TIME_INPUT_FORMATTER), true);
            }

            LocalDate date = LocalDate.parse(rawValue, DATE_INPUT_FORMATTER);
            return new TaskDateTime(date.atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            throw new RudraException("Please enter dates as yyyy-mm-dd or yyyy-mm-dd HHmm.");
        }
    }

    /**
     * Returns the normalized form used in the save file.
     *
     * @return Save-file representation.
     */
    public String toStorageString() {
        if (this.hasTime) {
            return this.value.format(DATE_TIME_INPUT_FORMATTER);
        }
        return this.value.toLocalDate().format(DATE_INPUT_FORMATTER);
    }

    /**
     * Returns the user-facing formatted date or date-time.
     *
     * @return Display text for the chatbot output.
     */
    public String toDisplayString() {
        if (this.hasTime) {
            return this.value.format(DATE_TIME_DISPLAY_FORMATTER);
        }
        return this.value.toLocalDate().format(DATE_DISPLAY_FORMATTER);
    }

    /**
     * Compares this value chronologically with another task date-time.
     *
     * @param other Date-time to compare with this value.
     * @return A negative value, zero, or a positive value when this value is earlier, equal, or later.
     */
    @Override
    public int compareTo(TaskDateTime other) {
        return this.value.compareTo(other.value);
    }
}
