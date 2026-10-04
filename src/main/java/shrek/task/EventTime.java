package shrek.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * A validated event time that can be compared with another time of the same kind.
 *
 * <p>Time-only values are interpreted as times on the same day. Date-time values
 * include an explicit date and use the date when checking event ordering.</p>
 */
public final class EventTime implements Comparable<EventTime> {
    private static final DateTimeFormatter MERIDIEM_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("h[:mm]a")
            .toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DATE_TIME_WITH_SPACE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    private final LocalDateTime value;
    private final boolean hasDate;
    private final String storageValue;

    private EventTime(LocalDateTime value, boolean hasDate, String storageValue) {
        this.value = value;
        this.hasDate = hasDate;
        this.storageValue = storageValue;
    }

    /**
     * Parses a supported event time.
     *
     * @param input a time such as {@code 2pm} or a date-time such as
     *              {@code 2026-09-13T14:00}.
     * @return the validated event time.
     * @throws IllegalArgumentException if the input is blank or malformed.
     */
    public static EventTime parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Event time cannot be blank.");
        }
        String normalizedInput = input.trim().replaceAll("\\s+", " ");

        try {
            return new EventTime(LocalDateTime.parse(normalizedInput, DATE_TIME_FORMAT), true, normalizedInput);
        } catch (DateTimeParseException ignored) {
            // Try the documented space-separated date-time form next.
        }
        try {
            return new EventTime(LocalDateTime.parse(normalizedInput, DATE_TIME_WITH_SPACE_FORMAT), true,
                    normalizedInput);
        } catch (DateTimeParseException ignored) {
            // Try time-only formats next.
        }
        try {
            LocalTime parsedTime = LocalTime.parse(normalizedInput, MERIDIEM_FORMAT);
            return new EventTime(LocalDate.of(1970, 1, 1).atTime(parsedTime), false, normalizedInput);
        } catch (DateTimeParseException ignored) {
            // Try the 24-hour clock format next.
        }
        try {
            LocalTime parsedTime = LocalTime.parse(normalizedInput, CLOCK_FORMAT);
            return new EventTime(LocalDate.of(1970, 1, 1).atTime(parsedTime), false, normalizedInput);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Unsupported event time format.", e);
        }
    }

    /**
     * Returns whether this value contains an explicit date.
     *
     * @return true for date-time values, false for time-only values.
     */
    public boolean hasDate() {
        return hasDate;
    }

    /**
     * Returns the validated value in the same format the user entered.
     *
     * @return the storage representation.
     */
    public String toStorageFormat() {
        return storageValue;
    }

    /**
     * Returns a normalized value for duplicate-task comparisons.
     *
     * @return a canonical value including whether a date was supplied.
     */
    public String toCanonicalValue() {
        return (hasDate ? "date:" : "time:") + value;
    }

    @Override
    public int compareTo(EventTime other) {
        if (other == null) {
            throw new IllegalArgumentException("An event time cannot be compared with null.");
        }
        if (hasDate != other.hasDate) {
            throw new IllegalArgumentException("Event times must use the same format.");
        }
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return storageValue;
    }
}
