package waddles;

/**
 * Represents a simple task with no associated date or time.
 */
public class ToDo extends Task {

    /**
     * Creates a ToDo task with the given description.
     *
     * @param description Description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public Task copy() {
        ToDo copy = new ToDo(getDescription());
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}