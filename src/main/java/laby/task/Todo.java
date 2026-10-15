package laby.task;

import laby.Parser;

/** Represents a task without a deadline or event period. */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        this(description, false);
    }

    /**
     * Creates a todo task with the supplied completion state.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is complete.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the todo in the format used for persistence.
     */
    @Override
    public String toFileString() {
        return "T" + Parser.FIELD_SEPARATOR + super.toFileString() + "\n";
    }
}
