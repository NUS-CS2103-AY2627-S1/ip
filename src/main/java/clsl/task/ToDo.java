package clsl.task;

/**
 * Represents a task without an associated date.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete to-do task with the specified description.
     *
     * @param name Description of the task.
     */
    public ToDo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns this to-do task in the storage format.
     *
     * @return The task prefixed with its to-do marker.
     */
    @Override
    public String getSaveString() {
        return "T | " + super.getSaveString();
    }
}
