package nano.task;

import nano.NanoException;

/**
 * Represents a todo task without a specific date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the specified description.
     *
     * @param description description of the todo task.
     * @throws NanoException if the description is empty.
     */
    public Todo(String description) throws NanoException {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + getStatusIcon() + " " + getDescription()
                + " (priority: " + getPriorityText() + ")";
    }
}
