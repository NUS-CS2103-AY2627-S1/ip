package ducky;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Represents a task with a due date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate by;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description the description of the task.
     * @param by the due date.
     */
    public Deadline(String description, LocalDate by) {
        this(description, by, List.of());
    }

    /**
     * Creates a deadline task with the given description, due date, and tags.
     *
     * @param description the description of the task.
     * @param by the due date.
     * @param tags the tags associated with the task.
     */
    public Deadline(String description, LocalDate by, List<String> tags) {
        super(description, tags);
        assert by != null : "A deadline must have a due date";
        this.by = by;
    }

    /**
     * Returns the due date for this task.
     *
     * @return the deadline.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this deadline in Ducky's display format.
     *
     * @return the formatted deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
