package bot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates a deadline task.
     *
     * @param description description of the task.
     * @param dueDate date by which the task must be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        assert dueDate != null : "Deadline date must not be null";

        this.dueDate = dueDate;
    }

    /**
     * Returns the deadline in the format used by the storage file.
     *
     * @return storage representation containing the task type and deadline date
     */
    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + dueDate;
    }

    /**
     * Checks whether this deadline falls on the specified date.
     *
     * @param date date to check.
     * @return {@code true} if the deadline is on the specified date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueDate.equals(date);
    }

    /**
     * Returns midnight on the deadline date as its chronological sort key.
     *
     * @return the deadline date at the start of the day
     */
    @Override
    public Optional<LocalDateTime> getChronologicalSortKey() {
        return Optional.of(dueDate.atStartOfDay());
    }

    /**
     * Checks whether another deadline has the same description and due date.
     *
     * @param other task to compare.
     * @return {@code true} if both deadlines have the same identifying details
     */
    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && dueDate.equals(((Deadline) other).dueDate);
    }

    /**
     * Returns the deadline's type, status, description, and formatted date for display.
     *
     * @return display representation of the deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_FORMAT) + ")";
    }
}
