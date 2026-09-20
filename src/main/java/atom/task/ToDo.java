package atom.task;

/**
 * Represents a task without a start or end time.
 */
public class ToDo extends Task {
    /**
     * Instantiates a <code>ToDo</code> task.
     *
     * @param description The description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
