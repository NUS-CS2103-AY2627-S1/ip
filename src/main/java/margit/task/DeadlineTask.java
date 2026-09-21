package margit.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Represents a task with a deadline. */
public class DeadlineTask extends Task {
    private static final String DEADLINE_TYPE = "D";

    private final TaskDateTime by;

    /** Creates a deadline task with its description and deadline. */
    public DeadlineTask(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    /** Returns whether this deadline falls on {@code date}. */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate deadlineDate = by.toLocalDate();
        return deadlineDate != null && deadlineDate.equals(date);
    }

    /** Returns the deadline time used to sort this task. */
    @Override
    public LocalDateTime getScheduledDateTime() {
        return by.toSortDateTime();
    }

    /** Returns the save-file representation of this deadline task. */
    @Override
    public String toSaveFormat() {
        return DEADLINE_TYPE + SAVE_FIELD_SEPARATOR + getSaveStatusAndDescription()
                + SAVE_FIELD_SEPARATOR + by.toSaveFormat();
    }

    /** Returns the user-facing representation with the deadline details. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
