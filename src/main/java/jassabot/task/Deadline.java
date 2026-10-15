package jassabot.task;

import java.time.LocalDateTime;

import jassabot.util.DateTimeFormats;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {

    private final LocalDateTime dueDateTime;

    /**
     * Creates a deadline task.
     *
     * @param description Work that must be completed.
     * @param dueDateTime Date and time by which the task is due.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        this.dueDateTime = dueDateTime;
    }

    /**
     * Converts this deadline into one line suitable for saving to the storage file.
     *
     * @return Storage-file representation of this deadline.
     */
    @Override
    public String toDataString() {
        return super.toDataString("D") + " | "
                + DateTimeFormats.formatForStorage(dueDateTime);
    }

    /**
     * Returns the type, completion status, description, and due date used to display this deadline.
     *
     * @return Display representation of this deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + DateTimeFormats.formatForDisplay(dueDateTime) + ")";
    }
}
