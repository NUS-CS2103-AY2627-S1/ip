package mono.task;

/**
 * Represents a task without an associated date.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param name task description
     */
    public ToDo(String name) {
        super(name);
    }

    /**
     * Returns this to-do's user-facing representation.
     *
     * @return task type, completion status, and task name
     */
    @Override
    public String toString() {
        return String.format("[T][%s] %s", super.isDone ? "X" : " ", super.name);
    }
}
