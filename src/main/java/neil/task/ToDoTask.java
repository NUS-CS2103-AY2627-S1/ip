package neil.task;

/**
 * Represents a task without date or time information.
 */
public class ToDoTask extends Task {
    /**
     * Creates a task with the specified description.
     *
     * @param description description of the task.
     */
    public ToDoTask(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String encode() {
        return String.format(
                "T | %d | %s",
                isDone ? 1 : 0,
                description
        );
    }
}
