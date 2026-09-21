package aster.task;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do in the command-line display format.
     *
     * @return to-do type marker, status marker, and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
