package cheecken;

/**
 * Represents a task without a deadline or event time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     * @param task description of the task
     */
    public Todo(String task) {
        super(task);
    }

    /**
     * Returns the todo display representation.
     * @return formatted task description and completion state
     */
    @Override
    public String toString() {
        return String.format("[T]" + super.toString());
    }

    /**
     * Serializes the todo task.
     * @return task record formatted for persistence
     */
    @Override
    public String toStorageString() {
        return "T | " + (isMarked ? "1" : "0") + " | " + task;
    }
}
