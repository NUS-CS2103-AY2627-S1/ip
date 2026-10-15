package dude.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task date with an optional time component.
 *
 * @param date     Date component.
 * @param dateTime Optional date-time component.
 */
public record TaskDate(LocalDate date, LocalDateTime dateTime) {
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_OUTPUT_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    /**
     * Validates that a timed value refers to the same date as its date field.
     *
     * @param date     Date component.
     * @param dateTime Optional date-time component.
     */
    public TaskDate {
        Objects.requireNonNull(date, "date");
        if (dateTime != null && !date.equals(dateTime.toLocalDate())) {
            throw new IllegalArgumentException("Date and date-time components differ");
        }
    }

    /**
     * Parses a date-only or date-time input.
     *
     * @param input Date in yyyy-MM-dd or date-time in yyyy-MM-dd HHmm format.
     * @return Parsed task date.
     * @throws DateTimeParseException If the input does not match a supported format.
     */
    public static TaskDate parse(String input) throws DateTimeParseException {
        String value = input == null ? "" : input.trim();
        if (value.length() == 10) {
            return new TaskDate(LocalDate.parse(value, DATE_INPUT_FORMAT), null);
        }
        if (value.length() == 15) {
            LocalDateTime dateTime = LocalDateTime.parse(value, DATE_TIME_INPUT_FORMAT);
            return new TaskDate(dateTime.toLocalDate(), dateTime);
        }
        throw new DateTimeParseException("Unsupported date format", value, 0);
    }

    /**
     * Parses a date-only input.
     *
     * @param input Date in yyyy-MM-dd format.
     * @return Parsed date.
     * @throws DateTimeParseException If the input is not a valid date-only value.
     */
    public static LocalDate parseDate(String input) throws DateTimeParseException {
        String value = input == null ? "" : input.trim();
        if (value.length() != 10) {
            throw new DateTimeParseException("Expected a date-only value", value, 0);
        }
        return LocalDate.parse(value, DATE_INPUT_FORMAT);
    }

    /**
     * Reconstructs a task date from its JSONL fields.
     *
     * @param date     Date field in yyyy-MM-dd format.
     * @param dateTime Date-time field, or an empty value for date-only input.
     * @return Reconstructed task date.
     */
    public static TaskDate fromStorage(String date, String dateTime) {
        LocalDate parsedDate = LocalDate.parse(date, DATE_INPUT_FORMAT);
        if (dateTime == null || dateTime.isBlank()) {
            return new TaskDate(parsedDate, null);
        }
        LocalDateTime parsedDateTime = LocalDateTime.parse(
                dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new TaskDate(parsedDate, parsedDateTime);
    }

    /**
     * Returns the value used for the date field in JSONL storage.
     *
     * @return ISO date value.
     */
    public String dateValue() {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Returns the value used for the optional date-time field in JSONL storage.
     *
     * @return ISO date-time value, or an empty string for date-only input.
     */
    public String dateTimeValue() {
        return dateTime == null
                ? ""
                : dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Returns a user-facing formatted value.
     *
     * @return Formatted date or date-time.
     */
    @Override
    public String toString() {
        return dateTime == null
                ? date.format(DATE_OUTPUT_FORMAT)
                : dateTime.format(DATE_TIME_OUTPUT_FORMAT);
    }

    /**
     * Returns whether this value occurs on the supplied date.
     *
     * @param targetDate Date to compare with.
     * @return True when the date components are equal.
     */
    public boolean occursOn(LocalDate targetDate) {
        return date.equals(targetDate);
    }

    /**
     * Returns a comparable date-time, treating a date-only value as midnight.
     *
     * @return Effective date-time value.
     */
    public LocalDateTime effectiveDateTime() {
        return dateTime == null ? date.atStartOfDay() : dateTime;
    }
}
