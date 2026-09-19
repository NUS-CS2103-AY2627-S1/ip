package xerxes.task;

/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete to-do task with the given description.
     *
     * @param taskName Description of the to-do task.
     */
    public ToDo(String taskName) {
        super(taskName);
    }

    /**
     * Returns a display representation prefixed with the to-do task type.
     *
     * @return Formatted to-do task description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

}
