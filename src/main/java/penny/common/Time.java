package penny.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles parsing and formatting of dates and optional times for tasks in Penny.
 * Encapsulates a {@link LocalDate} and optional {@link LocalTime}.
 */
public class Time {

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm")
    );

    private static final List<DateTimeFormatter> DATE_ONLY_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    );

    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma");
    private static final DateTimeFormatter STORAGE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter STORAGE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    private final LocalDate date;
    private final LocalTime time;

    /**
     * Constructs a Time instance by parsing a date (and optional time) string.
     *
     * @param input The date/time string to parse.
     * @throws PennyException If the input format does not match any accepted pattern.
     */
    public Time(String input) throws PennyException {
        String trimmed = input.trim();
        LocalDate parsedDate = null;
        LocalTime parsedTime = null;
        boolean isParsed = false;

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(trimmed, formatter);
                parsedDate = dateTime.toLocalDate();
                parsedTime = dateTime.toLocalTime();
                isParsed = true;
                break;
            } catch (DateTimeParseException ignored) {
                // Try next pattern
            }
        }

        if (!isParsed) {
            for (DateTimeFormatter formatter : DATE_ONLY_FORMATTERS) {
                try {
                    parsedDate = LocalDate.parse(trimmed, formatter);
                    parsedTime = null;
                    isParsed = true;
                    break;
                } catch (DateTimeParseException ignored) {
                    // Try next pattern
                }
            }
        }

        if (!isParsed) {
            throw new PennyException("Invalid date format: '" + input
                    + "'. Please use yyyy-MM-dd or d/M/yyyy (e.g. 2019-10-15 or 2/12/2019 1800).");
        }

        this.date = parsedDate;
        this.time = parsedTime;
    }

    /**
     * Parses a date string for search queries where time is not required.
     *
     * @param input The date string to parse.
     * @return The parsed {@link LocalDate}.
     * @throws PennyException If the input format is invalid.
     */
    public static LocalDate parseDate(String input) throws PennyException {
        String trimmed = input.trim();
        for (DateTimeFormatter formatter : DATE_ONLY_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next pattern
            }
        }
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmed, formatter).toLocalDate();
            } catch (DateTimeParseException ignored) {
                // Try next pattern
            }
        }
        throw new PennyException("Invalid date format: '" + input
                + "'. Please use yyyy-MM-dd or d/M/yyyy (e.g. 2019-10-15 or 2/12/2019).");
    }

    /**
     * Formats a date for user-friendly display (e.g., "Oct 15 2019").
     *
     * @param date The date to format.
     * @return The formatted date string.
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Returns the date component.
     *
     * @return The {@link LocalDate} instance.
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Checks if this instance contains a time component.
     *
     * @return True if a time is present, false otherwise.
     */
    public boolean hasTime() {
        return this.time != null;
    }

    /**
     * Returns the time component if present.
     *
     * @return The {@link LocalTime} instance, or null if date-only.
     */
    public LocalTime getTime() {
        return this.time;
    }

    /**
     * Checks if this date/time occurs on the specified date.
     *
     * @param targetDate The date to compare against.
     * @return True if the date matches targetDate, false otherwise.
     */
    public boolean isOnDate(LocalDate targetDate) {
        return this.date.equals(targetDate);
    }

    /**
     * Formats the date and optional time into storage file format.
     *
     * @return The formatted storage string (e.g., "2019-10-15" or "2019-10-15 1800").
     */
    public String toFileFormat() {
        if (this.time != null) {
            return this.date.format(STORAGE_DATE_FORMATTER) + " "
                    + this.time.format(STORAGE_TIME_FORMATTER);
        }
        return this.date.format(STORAGE_DATE_FORMATTER);
    }

    /**
     * Formats the date and optional time for user-friendly display.
     *
     * @return Formatted string (e.g., "Oct 15 2019" or "Oct 15 2019, 6:00pm").
     */
    @Override
    public String toString() {
        if (this.time != null) {
            return this.date.format(DISPLAY_DATE_FORMATTER) + ", "
                    + this.time.format(DISPLAY_TIME_FORMATTER).toLowerCase();
        }
        return this.date.format(DISPLAY_DATE_FORMATTER);
    }
}
