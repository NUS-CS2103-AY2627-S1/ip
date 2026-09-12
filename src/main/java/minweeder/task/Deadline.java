package minweeder.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    private final LocalDateTime by;

    /**
     * Creates a deadline task with the given description and due date/time.
     *
     * @param description the description of the task.
     * @param by the date and time the task is due by.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileString() {
        return "D | " + super.toFileFields() + " | " + this.by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public boolean isOccurringOn(LocalDate date) {
        return this.by.toLocalDate().equals(date);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Deadline)) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return this.getDescription().equals(otherDeadline.getDescription())
                && this.by.equals(otherDeadline.by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Deadline.class, this.getDescription(), this.by);
    }
}
