package panda.task;

import java.time.temporal.Temporal;

/**
 * Represents a task without a date or time requirement.
 */
public class Todo extends Task {

    /**
     * Creates an unmarked to-do task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Checks if this to-do task occurs on the specified date.
     * Always returns {@code false} as to-dos do not have date associations.
     *
     * @param date The date to check against.
     * @return Always {@code false}.
     */
    @Override
    public boolean occursOn(Temporal date) {
        return false;
    }

    /**
     * Returns a string representation of the to-do task.
     *
     * @return Formatted to-do string.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
