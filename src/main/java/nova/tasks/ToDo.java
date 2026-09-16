package nova.tasks;

/**
 * Represents a task without a specific date or time.
 */
public class ToDo extends Task {

    /**
     * Creates a ToDo task with the given description.
     *
     * @param description description of the task
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileString() {
        return "T | " + (isDone ? "X" : "O")
                + " | " + description;
    }
}
