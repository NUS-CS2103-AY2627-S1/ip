package shrekanddonkey.task;

/**
 * Represents a task without any associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a ToDo that is initially not done.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this task with the ToDo type marker.
     *
     * @return formatted ToDo text.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
