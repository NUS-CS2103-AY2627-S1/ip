package ada;

/**
 * Represents a simple todo task that only contains a description.
 * Subclass of {@code Task}, with no associated date‑time information.
 */
public class Todo extends Task {
    /**
     * Constructs a Todo task with the given description.
     * The task starts as unfinished.
     *
     * @param description text content of the todo task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns user‑facing string for console display.
     * Prepends "[T]" to identify it as a todo‑type task.
     *
     * @return formatted display string
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns formatted string used for writing to the storage file.
     * Uses prefix "T | " to mark todo entries.
     *
     * @return file‑storage compatible text line
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
