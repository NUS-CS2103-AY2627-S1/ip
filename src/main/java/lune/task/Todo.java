package lune.task;

/**
 * A task without any date/time attached to it.
 */
public class Todo extends Task {

    /**
     * Creates a new, not-done todo with the given description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Renders this todo for display, e.g. "[T][ ] read book".
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Renders this todo as one line of the on-disk save format, e.g.
     * "T | 0 | read book".
     */
    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }
}
