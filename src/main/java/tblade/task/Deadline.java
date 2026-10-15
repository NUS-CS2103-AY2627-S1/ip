package tblade.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private final LocalDate by;

    /**
     * Creates an unfinished deadline task.
     *
     * @param description text that describes the task
     * @param by the date by which the task must be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the deadline date for storage.
     *
     * @return the deadline date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the {@code (by: ...)} suffix shown after a deadline's description.
     *
     * @return the formatted due date, wrapped as a "by" suffix
     */
    @Override
    protected String getTimeDetails() {
        return " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns whether this deadline and the given object have the same type, description, and
     * due date.
     *
     * @param other the object to compare against
     * @return {@code true} if {@code other} is a deadline with the same description and due date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Deadline) || !super.equals(other)) {
            return false;
        }
        return by.equals(((Deadline) other).by);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code for this deadline's type, description, and due date
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), by);
    }
}
