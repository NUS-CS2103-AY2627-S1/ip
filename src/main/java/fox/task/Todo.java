package fox.task;

/**
 * Represents a task without a specific date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new unfinished to-do task.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do task in its display format.
     *
     * @return the formatted to-do task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
