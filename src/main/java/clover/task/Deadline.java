package clover.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");
    private final LocalDate endBy;

    /**
     * Creates a deadline with a description and due date.
     */
    public Deadline(String description, LocalDate endBy) {
        super(description);
        this.endBy = endBy;
    }

    /**
     * Creates a deadline that is optionally linked to a tutoree.
     */
    public Deadline(String description, LocalDate endBy, String tutoreeName) {
        super(description, tutoreeName);
        this.endBy = endBy;
    }

    /**
     * Returns the deadline text.
     */
    public LocalDate getEndBy() {
        return endBy;
    }

    /**
     * Returns this deadline in Clover's display format.
     */
    @Override
    public String toString() {
        return "[D] " + formatTaskDisplay()
                + " (by: " + endBy.format(DISPLAY_FORMAT) + ")" + formatTutoreeSuffix();
    }
}
