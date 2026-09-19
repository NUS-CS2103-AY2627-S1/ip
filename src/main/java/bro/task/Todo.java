package bro.task;

import java.time.LocalDate;

/**
 * Represents a todo task.
 * Uncompleted todos persist across all dates until marked done.
 */
public class Todo extends Task {
    /**
     * Constructs a Todo task with the specified description.
     *
     * @param description The description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Checks if this todo should be listed for a specific date.
     * Uncompleted todos are always listed because they remain active until
     * finished.
     *
     * @param date The LocalDate to check against.
     * @return True if the todo is not completed, false if completed.
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return !isDone();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileFormat() {
        return formatFilePrefix("T");
    }
}
