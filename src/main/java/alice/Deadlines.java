package alice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a specific deadline date.
 * Inherits from the Task class and adds a "by" date.
 */
public class Deadlines extends Task {
    protected LocalDate by;

    /**
     * Constructs a Deadline task with the given description and due date.
     *
     * @param description The task description.
     * @param by          The date by which the task must be completed.
     */
    public Deadlines(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date of the deadline task.
     *
     * @return The LocalDate representing the deadline.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * {@inheritDoc}
     * Returns the type icon for a Deadline task, which is "D".
     */
    @Override
    public String getTaskTypeIcon() {
        return "D";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task copy() {
        Deadlines copy = new Deadlines(description, by);
        copy.isDone = isDone;
        return copy;
    }

    /**
     * Returns the string representation of the deadline task.
     * The format is "[D][status] description (by: MMM d yyyy)".
     *
     * @return The formatted string.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
        return String.format("[D]%s (by: %s)", super.toString(), this.by.format(formatter));
    }

    /**
     * Returns the due date in ISO-8601 format (yyyy-MM-dd) for file storage.
     *
     * @return The date string.
     */
    public String getByForFile() {
        return by.toString();
    }
}
