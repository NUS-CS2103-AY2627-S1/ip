package jarvis.classes;

/** Represents a task without a deadline or event time. */
public class ToDo extends Task {

    /**
     * Creates a to-do task.
     *
     * @param taskName Description of the task.
     */
    public ToDo(String taskName) {
        super(taskName);
    }

    /**
     * Returns the to-do task in its display format.
     *
     * @return Formatted to-do task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
