package windy.task;

import windy.storage.TaskDataFormat;
import windy.storage.TaskDataFormat.TaskType;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a task without an associated date.
     *
     * @param description the task description
     * @param isDone whether the task has been completed
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /** {@inheritDoc} */
    @Override
    public Task copy() {
        return new Todo(getDescription(), isDone());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[T][" + this.getStatusIcon() + "] " + this.getDescription();
    }

    /** {@inheritDoc} */
    @Override
    public String toDataString() {
        return TaskDataFormat.formatRecord(TaskType.TODO, this.isDone(), this.getDescription());
    }
}
