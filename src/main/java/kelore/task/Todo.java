package kelore.task;

import kelore.storage.Storage;

/** Represents a task without a date or time. */
public class Todo extends Task {
    /**
     * Creates a to-do with the specified description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns a displayable description of this to-do and its completion status. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /** Returns this to-do in the format used by the data file. */
    @Override
    public String toStorageString() {
        return Storage.joinFields("T", getStorageStatus(), description);
    }

    /** Returns an independent copy of this to-do. */
    @Override
    Todo copy() {
        Todo copiedTodo = new Todo(description);
        if (isDone) {
            copiedTodo.markAsDone();
        }
        return copiedTodo;
    }
}
