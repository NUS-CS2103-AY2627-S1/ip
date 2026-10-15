package zen.task;

import java.time.LocalDate;

/** Represents a task without a date or time. */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description the todo description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates an incomplete todo task with a priority.
     *
     * @param description the todo description
     * @param priority the todo priority
     */
    public Todo(String description, Priority priority) {
        super(description, priority);
    }

    @Override
    public String toString() {
        return formatWithPriority(String.format("[T][%s] %s", getStatusIcon(), description));
    }

    /**
     * Returns this todo in the file format used for persistence.
     *
     * @return a pipe-delimited todo record
     */
    @Override
    public String toStorageString() {
        return String.format("T | %d | %s | %s", this.isDone ? 1 : 0, this.description,
                priority.getDisplayValue());
    }

    /**
     * Returns false, since a todo has no associated date and therefore
     * never occurs on any given date.
     *
     * @param date the date to compare against
     * @return false always
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return false;
    }
}
