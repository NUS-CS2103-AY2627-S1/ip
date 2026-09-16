package panda.model;

/**
 * Represents a to-do task in Panda.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description task description
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

}
