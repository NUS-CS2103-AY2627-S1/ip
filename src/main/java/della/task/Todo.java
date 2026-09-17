package della.task;

/**
 * Represents a todo task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task with the specified name.
     *
     * @param name Description of the todo task.
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Creates a todo task with the specified name and completion status.
     *
     * @param name Description of the todo task.
     * @param isDone Completion status of the todo task.
     */
    public Todo(String name, boolean isDone) {
        super(name);
        this.isDone = isDone;
    }

    @Override
    public String formatForStorage() {
        return String.format("%s|%s|%s", "T", super.isDone ? "1" : "0", this.name);
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
