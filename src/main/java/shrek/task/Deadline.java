package shrek.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * Represents a task that needs to be done before a specific date.
 */
public class Deadline extends Task {
    protected LocalDate by;

    /**
     * Creates a new deadline task.
     *
     * @param description the text describing the task.
     * @param by          the date by which the task should be done.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        // A deadline without a date cannot be displayed or saved correctly.
        assert by != null : "A deadline must have a due date.";
        this.by = by;
    }

    /**
     * Creates a new deadline task with tags.
     *
     * @param description the text describing the task.
     * @param by          the date by which the task should be done.
     * @param tags        the initial tags for the task.
     */
    public Deadline(String description, LocalDate by, Collection<String> tags) {
        super(description, tags);
        assert by != null : "A deadline must have a due date.";
        this.by = by;
    }

    @Override
    public String toSaveFormat() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by
                + getTagsStorageSuffix();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + by.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
    }

    @Override
    protected String getCanonicalDetails() {
        return super.getCanonicalDetails() + "|" + by;
    }
}
