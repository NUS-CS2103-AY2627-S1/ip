package org.cs2103t.marquee.cli.task;

import java.time.LocalDateTime;

import org.cs2103t.marquee.core.task.Task;
import org.cs2103t.marquee.core.time.DateTimeFormatter;

/**
 * Representation of a task that must be completed within a deadline.
 *
 * @see Task
 */
public final class DeadlineTask extends Task {
    /**
     * Creates a new {@code DeadlineTask} with the given description
     * and deadline, then mark it as either completed or incomplete.
     *
     * @param description the description of the task
     * @param deadline when the deadline is up
     * @param isMarked whether the task has been completed or not
     */
    public DeadlineTask(String description, LocalDateTime deadline, boolean isMarked) {
        super(description, isMarked, null, deadline, TaskTags.DEADLINE_TAG);
    }

    /**
     * Creates a new {@code DeadlineTask} with the given description
     * and deadline, then mark it as incomplete.
     *
     * @param description the description of the task
     * @param deadline when the deadline is up
     */
    public DeadlineTask(String description, LocalDateTime deadline) {
        this(description, deadline, false);
    }

    private DeadlineTask() {
        super();
    }

    /**
     * Get the deadline of this task.
     * <p>
     * Alias of {@link #getEnd()}.
     *
     * @return the deadline of this task
     */
    public LocalDateTime getDeadline() {
        return this.getEnd();
    }

    @Override
    public String toString() {
        return super.toString()
                + " (complete by " + DateTimeFormatter.formatDateTime(this.getDeadline()) + ")";
    }

    @Override
    public void setStart(LocalDateTime newStart) {
        if (newStart != null) {
            throw new UnsupportedOperationException("Deadline task doesn't have a starting time");
        }
    }
}
