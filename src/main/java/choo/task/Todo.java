package choo.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete ToDo with the given description.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this task with the ToDo type marker.
     *
     * @return formatted ToDo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
