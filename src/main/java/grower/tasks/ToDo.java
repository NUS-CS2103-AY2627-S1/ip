package grower.tasks;

/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do task with the specified description.
     *
     * @param description Description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toFileString() {
        return String.format(
                "T | %d | %s",
                isCompleted() ? 1 : 0,
                getDescription()
        );
    }

    /**
     * Returns a display string for this to-do task.
     *
     * @return Display representation of this to-do task.
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
