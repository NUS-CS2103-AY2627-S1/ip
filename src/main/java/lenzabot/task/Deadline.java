package lenzabot.task;

import java.time.LocalDateTime;

import lenzabot.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task with the given description and due date-time.
     *
     * @param description Description of what the task involves.
     * @param by Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    @Override
    public String toString() {
        return String.format("%s (by: %s)", super.toString(), DateTimeParser.format(this.by));
    }

    @Override
    public String toSaveFormat() {
        return String.join(SAVE_FILE_SEPARATOR, super.toSaveFormat(), this.by.toString());
    }
}
