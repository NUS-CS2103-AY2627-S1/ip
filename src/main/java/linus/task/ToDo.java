package linus.task;

/**
 * Represents a type of Task that is to be done.
 */
public class ToDo extends Task {
    /**
     * Creates a Todo Task.
     *
     * @param isDone Whether the task has been marked as completed.
     * @param description Description of the task.
     */
    public ToDo(boolean isDone, String description) {
        super(isDone, description);
    }

    /**
     * Returns a String representation of each ToDo task.
     * All event tasks are represented starting with [T].
     *
     * @return String representation of each ToDo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns a String representation of each ToDo task that
     * is formatted for ease of storing into the storage file.
     *
     * @return Formatted String representation of ToDo task.
     */
    @Override
    public String toFileFormat() {
        if (this.isDone()) {
            return "T | X | " + this.getDescription() + "\n";
        }
        return "T | | " + this.getDescription() + "\n";
    }

    /**
     * Returns if the input object is equal to the todo task.
     * Todo tasks are equal if they have the same description.
     * @param obj   the reference object with which to compare.
     * @return Whether the 2 objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ToDo) {
            ToDo task = (ToDo) obj;
            return this.getDescription().equals(task.getDescription());
        }
        return false;
    }
}
