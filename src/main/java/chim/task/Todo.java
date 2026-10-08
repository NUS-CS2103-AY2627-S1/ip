package chim.task;

/**
 * Represents a simple task with only a description and no date/time
 * attached to it.
 */
public class Todo extends Task {

    /**
     * Creates a Todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
