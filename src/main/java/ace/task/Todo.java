package ace.task;

/**
 * A task with a description only, with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new, incomplete todo with the given description.
     *
     * @param description the text describing what this todo is.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the representation of this todo used when saving it to disk. A todo
     * has no extra fields beyond its description and completion status, so this is
     * identical to {@link #toString()}.
     *
     * @return the save-file representation of this todo.
     */
    @Override
    public String loadFormat() {
        return this.toString();
    }

    /**
     * Returns this todo's display representation, e.g. "[T][ ] borrow book".
     *
     * @return this todo formatted for display.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
