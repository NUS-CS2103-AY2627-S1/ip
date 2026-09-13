package xuan.task;

/**
 * Represents a todo task without any additional date or time information.
 */
public class Todo extends Task {

    /**
     * Creates a Todo task with the given description.
     *
     * @param description the description of the todo task
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
