package chausistant.task;

import java.time.LocalDateTime;

/** A task that must be completed by a specified time or date. */
public class DeadlineTask extends Task {
    private final LocalDateTime deadline;
    private final boolean hasDeadlineTime;

    /** Creates a deadline task with its parsed due date and time. */
    public DeadlineTask(String item, LocalDateTime deadline, boolean hasDeadlineTime) {
        super(item);
        // Parser and storage convert valid date text into a non-null deadline before constructing this task.
        assert deadline != null : "Deadline tasks must have a deadline.";
        this.deadline = deadline;
        this.hasDeadlineTime = hasDeadlineTime;
    }

    /** Returns this deadline's date and time for filtering and sorting. */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    /** Returns whether another deadline has the same date and time details. */
    @Override
    protected boolean hasSameSchedulingDetails(Task other) {
        DeadlineTask otherDeadline = (DeadlineTask) other;
        return deadline.equals(otherDeadline.deadline)
                && hasDeadlineTime == otherDeadline.hasDeadlineTime;
    }

    /** Returns this deadline task in the chatbot's display format. */
    @Override
    public String printTask() {
        return "[D]" + getStatusMark() + " " + getItem()
                + " (by: " + formatDateTimeForDisplay(deadline, hasDeadlineTime) + ")";
    }

    /** Returns this deadline task as one save-file line. */
    @Override
    public String toSaveFormat() {
        return formatSaveLine("D", getSaveStatus(), getItem(),
                formatDateTimeForSave(deadline, hasDeadlineTime));
    }
}
