package jiji.task;

/**
 * Represents a todo task without any date or time constraints.
 */
public class Todo extends Task {

    /**
     * Constructs a new Todo task with the specified description.
     *
     * @param description Description of the todo item.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the storage text representation for a Todo task prefixed with its task type code.
     *
     * @return Formatted string for file storage: {@code "T | <isDone> | <description>"}.
     */
    @Override
    public String toFileFormat() {
        return TaskType.TODO.getCode() + " | " + super.toFileFormat();
    }

    /**
     * Returns the string representation of the todo task, prefixed with [T].
     *
     * @return Formatted todo string representation.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
