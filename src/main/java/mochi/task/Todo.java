package mochi.task;

/**
 * Represents a task without any date/time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }

    @Override
    public String toString() {
        return "[" + type.getSymbol() + "]" + super.toString();
    }
}
