package aider.model;

import java.time.LocalDateTime;

import aider.util.DateTimeParser;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The date or time by which the task should be completed. */
    private final LocalDateTime by;

    /** The date or time text entered when the deadline was created. */
    private final String scheduleText;

    /**
     * Creates a deadline task.
     *
     * @param description the text describing the task
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, by, DateTimeParser.format(by));
    }

    /**
     * Creates a deadline task while preserving its original schedule text.
     *
     * @param description the text describing the task
     * @param by the parsed deadline date and time
     * @param scheduleText the date or time text entered by the user
     */
    public Deadline(String description, LocalDateTime by, String scheduleText) {
        super(description, TaskType.DEADLINE);
        assert by != null : "Deadline time must not be null";
        this.by = by;
        this.scheduleText = scheduleText;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return the deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    protected String getScheduleText() {
        return scheduleText;
    }

    @Override
    protected String getTaskDetails() {
        return description + " (by: " + DateTimeParser.format(by) + ")";
    }

    @Override
    public String toFileString() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | "
                + DateTimeParser.toStorageString(by);
    }
}
