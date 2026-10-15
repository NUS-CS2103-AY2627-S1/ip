package catgpt;

/**
 * Represents a task without any associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Task description.
     */
    public Todo(String description) {
        this(description, false);
    }

    /**
     * Reconstructs a todo task with its stored completion state.
     *
     * @param description Task description.
     * @param isDone Whether the task has been completed.
     */
    Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public TaskType getType() {
        return TaskType.TODO;
    }

    @Override
    public String getStorageDetails() {
        return "";
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
