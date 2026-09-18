package luna.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that must be completed by a deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH);
    private final LocalDate byDate;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description Description of the deadline task.
     * @param byDate Deadline of the task.
     */
    public Deadline(String description, LocalDate byDate) {
        super(description);
        this.byDate = Objects.requireNonNull(byDate, "A deadline date must not be null.");
    }

    /**
     * Returns the deadline text of this task.
     *
     * @return Deadline text.
     */
    public LocalDate getByDate() {
        return byDate;
    }

    /**
     * Returns whether another deadline has the same description and due date.
     *
     * @param other Task to compare with this deadline.
     * @return {@code true} if both deadlines have the same details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && byDate.equals(((Deadline) other).byDate);
    }

    /**
     * Returns the save-file representation of this deadline task.
     *
     * @return Serialized representation used in storage.
     */
    @Override
    public String toStorageString() {
        return "D | " + getStorageStatus() + " | " + getDescription() + " | " + byDate;
    }

    /**
     * Returns the user-facing text representation of this deadline task.
     *
     * @return Formatted deadline text for display.
     */
    @Override
    public String toString() {
        return "[D]" + getStatusIcon() + " "
                + getDescription()
                + " (by: " + byDate.format(OUTPUT_FORMAT) + ")";
    }
}
