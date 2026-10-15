package alzara.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A task with a description, a done/not-done state, and a due date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate deadline;

    /**
     * Creates a deadline that starts out not done.
     *
     * @param description the task's description
     * @param deadline the date the task is due by
     */
    public Deadline(String description, LocalDate deadline) {
        super(description);
        this.deadline = deadline;
    }

    /**
     * Returns this task's save-file line, prefixed with the type letter and
     * suffixed with the due date.
     *
     * @return the save-file line, e.g. {@code "D | N | return book | 2019-10-15"}
     */
    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + this.deadline;
    }

    /**
     * Returns this task's console display, prefixed with the type letter and
     * suffixed with the due date.
     *
     * @return the display line, with the due date as {@code "(by: MMM dd yyyy)"}
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.deadline.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public boolean isScheduledOn(LocalDate date) {
        return this.deadline.equals(date);
    }

    @Override
    public LocalDate getSortDate() {
        return this.deadline;
    }

    /**
     * Returns true if {@code other} is a {@link Deadline} with the same
     * description (per {@link Task#equals}) and the same due date. Used to
     * detect duplicate tasks when adding a new one.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Deadline deadline = (Deadline) other;
        return this.deadline.equals(deadline.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deadline);
    }
}
