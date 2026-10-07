package drax;

/**
 * A task without a deadline or event time range.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param task description of the task.
     */
    public Todo(String task) {
        super(task);
    }

    @Override
    public String toString() {
        return String.format("[T][%s] %s", this.getStatusIcon(), this.getTask());
    }
}
