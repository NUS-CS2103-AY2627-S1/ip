package bot.task;

/**
 * A task with no date/time attached to it.
 */
public class Todo extends Task {

    /**
     * Creates a new, not-done todo with the given description.
     *
     * @param description The todo's description text.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo's status icon and description, prefixed with the {@code [T]} type icon.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns this todo's data-file line, e.g. {@code "T | 1 | read book"}.
     */
    @Override
    public String toSaveFormat() {
        return "T | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }
}
