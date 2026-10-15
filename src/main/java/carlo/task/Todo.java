package carlo.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do task in the command-line display format.
     *
     * @return the task type, completion status, and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns this to-do task in the on-disk save format.
     *
     * @return the type letter, completion flag, and description, separated by " | "
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
