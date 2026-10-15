package walnut;

/**
 * Represents a task without a date or time constraint.
 */
public class ToDo extends Task {

    /**
     * Creates a to-do task with the specified description.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
