package caitlyn;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents an action that can be completed once within an inclusive date/time window.
 */
public final class WithinTask extends Task {
    /** The start boundary, retaining whether the user supplied a time. */
    private final DateTimeParser.ParsedDateTime from;

    /** The end boundary, retaining whether the user supplied a time. */
    private final DateTimeParser.ParsedDateTime to;

    /**
     * Creates an incomplete task with a validated window.
     *
     * @param description the nonblank task description.
     * @param from the start date or local date/time.
     * @param to the end date or local date/time.
     * @throws IllegalArgumentException if a field is invalid or the window is reversed.
     */
    public WithinTask(String description, String from, String to) {
        super(requireDescription(description));
        this.from = parseBoundary(from, true);
        this.to = parseBoundary(to, false);

        // A date-only end includes the entire day, without changing its stored representation.
        LocalDateTime effectiveEnd = this.to.hasTime() ? this.to.value()
                : this.to.value().toLocalDate().atTime(LocalTime.MAX);
        if (this.from.value().isAfter(effectiveEnd)) {
            throw new IllegalArgumentException(
                    "I beg your pardon, master. The start of the window must not be after its end.");
        }
    }

    /**
     * Returns the within-period marker used by inherited display and storage behavior.
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.WITHIN;
    }

    /**
     * Returns the description and readable inclusive window.
     */
    @Override
    protected String getTaskDetails() {
        return super.getTaskDetails() + " (within: " + DateTimeParser.formatForDisplay(from)
                + " to: " + DateTimeParser.formatForDisplay(to) + ")";
    }

    /**
     * Returns the description and canonical boundaries for the inherited escaping logic.
     */
    @Override
    protected List<String> getStorageFields() {
        return List.of(getDescription(), DateTimeParser.formatForStorage(from),
                DateTimeParser.formatForStorage(to));
    }

    /**
     * Rejects absent descriptions and normalizes their surrounding whitespace.
     */
    private static String requireDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A within-period task description cannot be empty.");
        }
        return description.trim();
    }

    /**
     * Parses one boundary and rejects precision that the task display would conceal.
     */
    private static DateTimeParser.ParsedDateTime parseBoundary(String text, boolean isStart) {
        String boundary = isStart ? "start" : "end";
        String example = isStart ? "2027-01-15 or 15/1/2027 0900" : "2027-01-25 or 25/1/2027 1700";
        DateTimeParser.ParsedDateTime parsed;
        try {
            parsed = DateTimeParser.parse(text);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("I beg your pardon, master. Please provide a valid "
                    + boundary + " date, for example: " + example + ".", exception);
        }
        if (parsed.value().getSecond() != 0 || parsed.value().getNano() != 0) {
            throw new IllegalArgumentException("I beg your pardon, master. The " + boundary
                    + " time must use minute precision; seconds and fractional seconds must be zero.");
        }
        return parsed;
    }
}
