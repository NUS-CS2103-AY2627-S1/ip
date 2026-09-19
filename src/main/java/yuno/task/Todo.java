package yuno.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task with the specified description and completion status.
     *
     * @param description Text that describes the task.
     * @param isDone Whether the task is completed.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public boolean isRelevantFor(LocalDate date) {
        return true;
    }

    @Override
    public String toStorageString() {
        return String.format("T | %c | %s", getStatus(), getDescription());
    }

    @Override
    public LocalDateTime getChronologicalTime() {
        return LocalDateTime.MAX;
    }

    @Override
    Task copy() {
        return new Todo(getDescription(), getStatus() == 'X');
    }

    /**
     * Returns this to-do task in a display-ready format.
     *
     * @return Formatted to-do task description and status.
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
