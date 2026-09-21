package caitlyn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The date and optional time by which the task should be completed. */
    private final LocalDateTime by;

    /** Whether the deadline input explicitly included a time. */
    private final boolean hasByTime;

    /**
     * Creates a new incomplete deadline task.
     *
     * @param description the text describing the task.
     * @param by the date or time by which the task should be completed.
     * @throws IllegalArgumentException if a task field is null or the date is invalid.
     */
    public Deadline(String description, String by) {
        this(description, DateTimeParser.parse(by));
    }

    /**
     * Creates a deadline from a date without a time.
     *
     * @param description the task description.
     * @param by the date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        this(description, convertDateOnly(by));
    }

    /**
     * Creates a deadline from a date and time.
     *
     * @param description the task description.
     * @param by the date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, new DateTimeParser.ParsedDateTime(by, true));
    }

    /**
     * Creates a deadline from an already parsed date/time value.
     */
    private Deadline(String description, DateTimeParser.ParsedDateTime by) {
        super(description);
        if (by == null) {
            throw new IllegalArgumentException("A deadline cannot be null.");
        }
        this.by = by.value();
        this.hasByTime = by.hasTime();
    }

    /**
     * Returns the deadline date and optional time.
     *
     * @return the date and optional time attached to this deadline.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the deadline task type used for display and storage.
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns the description followed by the formatted deadline.
     *
     * @return the display text for this deadline.
     */
    @Override
    protected String getTaskDetails() {
        return super.getTaskDetails() + " (by: "
                + DateTimeParser.formatForDisplay(by, hasByTime) + ")";
    }

    /**
     * Returns the base task fields followed by the stored deadline value.
     *
     * @return fields used to serialize this deadline.
     */
    @Override
    protected List<String> getStorageFields() {
        List<String> fields = new ArrayList<>(super.getStorageFields());
        fields.add(DateTimeParser.formatForStorage(by, hasByTime));
        return fields;
    }

    /**
     * Converts a date-only constructor argument into the shared parsed representation.
     */
    private static DateTimeParser.ParsedDateTime convertDateOnly(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("A deadline cannot be null.");
        }
        return new DateTimeParser.ParsedDateTime(date.atStartOfDay(), false);
    }
}
