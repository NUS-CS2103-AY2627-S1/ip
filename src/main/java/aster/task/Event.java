package aster.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents a task that takes place between a specified start and end time.
 */
public class Event extends Task {
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the event
     * @param from date or time at which the event starts
     * @param to date or time at which the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Checks whether this event occurs on the given date.
     *
     * @param targetDate date to check
     * @return {@code true} when the target date falls within the event interval
     */
    public boolean occursOn(LocalDate targetDate) {
        LocalDateTime start = parseDateTime(from).orElse(null);
        LocalDateTime end = parseDateTime(to).orElse(null);
        if (start == null || end == null) {
            return false;
        }
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        return !targetDate.isBefore(startDate) && !targetDate.isAfter(endDate);
    }

    /**
     * Returns this event in the command-line display format.
     *
     * @return event type marker, status marker, description, start text, and end text
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this event in the persistent storage format.
     *
     * @return storage line containing task type, status, description, start text, and end text
     */
    @Override
    public String toStorageString() {
        return "E | " + (isDone() ? "1" : "0") + " | " + getDescription() + " | " + from + " | " + to
                + getStorageTagSuffix();
    }

    /**
     * Returns the raw start text entered for this event.
     *
     * @return raw start text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the raw end text entered for this event.
     *
     * @return raw end text
     */
    public String getTo() {
        return to;
    }

    /**
     * Attempts to parse text as a supported date or date-time.
     *
     * @param text raw date/time text
     * @return parsed date-time when successful, otherwise {@link Optional#empty()}
     */
    public static Optional<LocalDateTime> tryParseDateTime(String text) {
        return parseDateTime(text);
    }

    /**
     * Attempts to parse the given text as a date or date-time in one of the supported formats.
     *
     * @param text raw start or end text
     * @return parsed date-time, or {@code null} when the text does not match any supported format
     */
    private static Optional<LocalDateTime> parseDateTime(String text) {
        if (text == null) {
            return Optional.empty();
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return Optional.of(LocalDateTime.parse(trimmed, formatter));
            } catch (DateTimeParseException ignored) {
                // fall through to next format
            }
        }

        // Some date-only strings are parsed by LocalDate.parse but not LocalDateTime.parse,
        // so convert them to start-of-day for range comparisons.
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(trimmed, formatter);
                return Optional.of(date.atStartOfDay());
            } catch (DateTimeParseException ignored) {
                // fall through to next format
            }
        }
        return Optional.empty();
    }
}
