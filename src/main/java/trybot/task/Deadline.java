package trybot.task;

import java.time.LocalDateTime;

import trybot.parser.DateTimeParser;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final String byText;
    private final LocalDateTime byDateTime;
    private final boolean byHasTime;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description text describing the task.
     * @param by date or time by which the task should be completed.
     */
    public Deadline(String description, String by) {
        super(description);
        if (by == null || by.isBlank()) {
            throw new IllegalArgumentException("A deadline date or time cannot be blank.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A deadline description cannot be blank.");
        }
        DateTimeParser.ParsedDateTime parsedBy = DateTimeParser.parseOrNull(by);
        String trimmedBy = by.trim();
        this.byText = parsedBy == null ? trimmedBy : null;
        this.byDateTime = parsedBy == null ? null : parsedBy.dateTime();
        this.byHasTime = parsedBy != null && parsedBy.hasTime();

        // A deadline is represented by exactly one of legacy text or a parsed date-time.
        assert (byText == null) != (byDateTime == null);
    }

    /**
     * Returns the typed deadline when the input was a supported numeric date or date-time.
     *
     * @return deadline date-time, or null for legacy descriptive text
     */
    public LocalDateTime getByDateTime() {
        return byDateTime;
    }

    /**
     * Returns the deadline as it should be shown to the user.
     *
     * @return formatted date-time or the original descriptive text
     */
    public String getBy() {
        if (byDateTime == null) {
            return byText;
        }
        return DateTimeParser.formatForDisplay(new DateTimeParser.ParsedDateTime(byDateTime, byHasTime));
    }

    /**
     * Returns the canonical deadline value used in the task data file.
     *
     * @return storage-formatted date-time or the original descriptive text
     */
    private String getByStorageValue() {
        if (byDateTime == null) {
            return byText;
        }
        return DateTimeParser.formatForStorage(new DateTimeParser.ParsedDateTime(byDateTime, byHasTime));
    }

    /**
     * Formats this deadline for the task data file.
     *
     * @return the task type, completion status, description, and deadline
     */
    @Override
    public String toStorageString() {
        return "D | " + (isDone ? "1" : "0") + " | " + escapeStorageField(description)
                + " | " + escapeStorageField(getByStorageValue()) + getTagsForStorage();
    }

    /**
     * Formats this task with its deadline.
     *
     * @return formatted deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getBy() + ")";
    }
}
