package aravii;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description The task description.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    TaskType getType() {
        return TaskType.TODO;
    }

    @Override
    String formatDetails() {
        return "";
    }

    @Override
    String formatStorageDetails() {
        return "";
    }
}
