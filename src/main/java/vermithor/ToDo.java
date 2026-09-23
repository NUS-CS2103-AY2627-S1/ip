package vermithor;

/**
 * Represents a task with no date or time associated with it.
 */
public class ToDo extends Task {

    /**
     * Creates an incomplete to-do task.
     *
     * @param description the to-do description
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the to-do format used in the task list.
     *
     * @return the task type, status, and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
