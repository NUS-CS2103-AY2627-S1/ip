package sophon.model;

/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description details of the todo.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * Returns this todo in the format used by the save file.
     *
     * @return save file representation of this todo.
     */
    @Override
    public String toFileString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }
}
