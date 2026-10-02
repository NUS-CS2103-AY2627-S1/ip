package ari.task;

/**
 * Represents a task without a deadline or scheduled time.
 */
public class TodoTask extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Todo description.
     */
    public TodoTask(String description) {
        super(description, "T");
    }

    @Override
    public TodoTask copy() {
        TodoTask copy = new TodoTask(this.description);
        copy.isDone = this.isDone;
        return copy;
    }
}
