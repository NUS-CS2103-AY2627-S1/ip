package wiz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HHmm", Locale.US);
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final LocalDateTime by;

    /**
     * Constructs a Deadline task with description and due date/time.
     *
     * @param description The task description.
     * @param by The due date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline due time cannot be null";
        this.by = by;
    }

    /**
     * Returns the due date and time of the deadline.
     *
     * @return The due LocalDateTime.
     */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Deadline deadline = (Deadline) obj;
        return Objects.equals(by, deadline.by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), by);
    }

    @Override
    public String toString() {
        return "[D]"
                + super.toString()
                + " (by: "
                + by.format(DISPLAY_FORMATTER)
                + ")";
    }

    @Override
    public String toFileString() {
        return "D | "
                + (isDone ? "1" : "0")
                + " | "
                + description
                + " | "
                + by.format(FILE_FORMATTER);
    }
}