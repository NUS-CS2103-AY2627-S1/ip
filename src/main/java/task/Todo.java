package task;

import michael.MichaelException;

/**
 * Represents a task without a deadline or event time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description the text describing the task
     * @throws MichaelException if the task description is empty
     */
    public Todo(String description) throws MichaelException {
        super(description, "todo");
    }

    /**
     * Returns the text used to display this to-do task.
     *
     * @return the to-do task type, status, and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
