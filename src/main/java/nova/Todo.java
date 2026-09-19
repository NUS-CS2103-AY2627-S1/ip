package nova;

/**
 * Represents a todo, a task without any date attached.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description the todo description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a display string of this todo, e.g. "[T][ ] read book".
     *
     * @return the display string
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
