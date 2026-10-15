package rocky.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final LocalDateTime dueDateTime;
    private final boolean hasDueTime;

    /**
     * Creates a deadline task with a description and due-time text.
     *
     * @param description the text describing the task
     * @param dueDate the date by which the task is due
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDateTime = dueDate.atStartOfDay();
        this.hasDueTime = false;
    }

    /**
     * Creates a deadline with a date and time.
     *
     * @param description the text describing the task
     * @param dueDateTime the date and time by which the task is due
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        this.dueDateTime = dueDateTime;
        this.hasDueTime = true;
    }

    /** Returns the deadline used for chronological sorting.
     *
     * @return this task's deadline.
     */
    @Override
    public Optional<LocalDateTime> getSortDate() {
        return Optional.of(dueDateTime);
    }

    /**
     * Returns this deadline task in the chatbot's display format.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        DateTimeFormatter format = hasDueTime ? DISPLAY_DATE_TIME_FORMAT : DISPLAY_DATE_FORMAT;
        return "[D][" + getStatusIcon() + "] " + getDescription()
                + " (by: " + dueDateTime.format(format) + ")";
    }

    /**
     * Returns the type, status, description, and due time in the task file format.
     *
     * @return the storage representation of this deadline
     */
    @Override
    public String toStorageString() {
        String storedDate = hasDueTime ? dueDateTime.toString() : dueDateTime.toLocalDate().toString();
        return "D | " + getStatusValue() + " | " + getDescription() + " | " + storedDate;
    }
}
