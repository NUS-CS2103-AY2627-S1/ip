package carl.task;

/**
 * Represents a Todo task without any date or time constraints.
 */
public class Todo extends Task {

    /**
     * Constructs a new Todo task with the specified item.
     * The task status is initialized to NOT_DONE by default.
     *
     * @param name The description or name of the todo task.
     */
    public Todo(String name) {
        super(name, TaskType.TODO);

    }

    /**
     * Constructs a Todo task with a specified completion status.
     * This is typically used when loading tasks from a saved file.
     *
     * @param name   The description or name of the todo task.
     * @param status The completion status of the task.
     */
    public Todo(String name, TaskStatus status) {
        super(name, status, TaskType.TODO);

    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

