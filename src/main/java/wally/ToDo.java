package wally;

/**
 * Represents a task with a description and no timing.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do task with the specified name.
     *
     * @param name Name of the task.
     */
    public ToDo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String getCommand() {
        return "todo " + super.getCommand();
    }
}
