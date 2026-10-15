package larry.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a particular date or time.
 */
public class Deadline extends Task {
    private TaskDateTime dueDateTime;

    /**
     * Creates a deadline task from date and time text.
     *
     * @param description Description of the task.
     * @param dueDateText Date and time text accepted by Larry.
     */
    public Deadline(String description, String dueDateText) {
        super(description);
        this.dueDateTime = new TaskDateTime(dueDateText);
    }

    /**
     * Creates a deadline task with a parsed date and time.
     *
     * @param description Description of the task.
     * @param dueDateTime Date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        this.dueDateTime = new TaskDateTime(dueDateTime);
    }

    /**
     * Creates a deadline task with an already parsed task date and time.
     *
     * @param description Description of the task.
     * @param dueDateTime Date and time by which the task should be completed.
     */
    public Deadline(String description, TaskDateTime dueDateTime) {
        super(description);
        this.dueDateTime = dueDateTime;
    }

    /**
     * Returns the deadline's date and time value.
     *
     * @return Date and time attached to the deadline.
     */
    public TaskDateTime getDueDateTime() {
        return dueDateTime;
    }

    /**
     * Replaces this deadline's due date and time after parsing the new value.
     *
     * @param dueDateText New date and time text accepted by Larry.
     */
    public void updateDueDateTime(String dueDateText) {
        TaskDateTime updatedDateTime = new TaskDateTime(dueDateText);
        this.dueDateTime = updatedDateTime;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return dueDateTime.isOn(date);
    }

    /**
     * Returns the task in the format used by Larry's responses.
     *
     * @return Status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDateTime + ")";
    }
}
