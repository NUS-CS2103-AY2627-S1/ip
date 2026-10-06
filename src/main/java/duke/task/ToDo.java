package duke.task;

/**
 * Represents a to-do task.
 */
public class ToDo extends Task {
    /**
     * Constructs a ToDo task with the given description.
     *
     * @param name The description of the to-do task.
     */
    public ToDo(String name) {
        super(name);
    }

    /**
     * Returns the task type icon for a to-do.
     *
     * @return The string "T".
     */
    @Override
    public String getTaskIcon() {
        return "T";
    }

    /**
     * Creates and returns a copy of this ToDo task.
     *
     * @return A copy of this ToDo task.
     */
    @Override
    public ToDo copy() {
        ToDo copy = new ToDo(getName());
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }
}
