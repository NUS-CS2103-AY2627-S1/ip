package drax;

import java.time.LocalDateTime;

/**
 * A task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    protected LocalDateTime deadline;

    /**
     * Creates a deadline task.
     *
     * @param task description of the task.
     * @param deadline date and time by which the task should be completed.
     */
    public Deadline(String task, LocalDateTime deadline) {
        super(task);
        this.deadline = deadline;
    }

    /**
     * Returns the deadline assigned to this task.
     *
     * @return the task deadline
     */
    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    @Override
    public String toString() {
        return String.format("[D][%s] %s (by: %s)", this.getStatusIcon(), this.task,
                ScheduleDateTime.formatForDisplay(this.deadline));
    }
}
