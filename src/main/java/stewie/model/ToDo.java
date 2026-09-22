package stewie.model;

/**
 * Represents a task of type ToDo.
 */
public class ToDo extends Task {
    /**
     * Creates an unfinished todo with a validated description.
     *
     * @param description Description of the task.
     * @throws IllegalArgumentException If the description is null, blank, or contains a pipe or control character.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the todo type marker, completion marker, and description.
     */
    @Override
    public String toString() {
        return String.format("[T] [%s] %s", super.getStatusIcon(), super.getDescription());
    }
}
