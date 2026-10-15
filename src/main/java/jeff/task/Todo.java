package jeff.task;

/**
 * A task with no date or time attached to it, e.g. "visit new theme park".
 */
public class Todo extends Task {
    /**
     * Creates a new, not-done todo with the given description.
     *
     * @param description what the task is; must not be null or blank.
     * @throws IllegalArgumentException if description is null or blank.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo's status icon and description, prefixed with "[T]". */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /** Returns this todo's data encoded for {@link jeff.storage.Storage}, prefixed with "T". */
    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }
}
