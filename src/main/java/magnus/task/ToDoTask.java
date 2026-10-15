package magnus.task;

/**
 * Represents a task without an associated date or time.
 */
public class ToDoTask extends Task {

    /**
     * Creates an incomplete to-do task with the specified description.
     *
     * @param description The description of the task.
     */
    public ToDoTask(String description) {
        super(description);
    }

    /**
     * Returns a string representation containing the to-do marker, completion status,
     * and description.
     *
     * @return The string representation of this to-do task.
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
