package dingleberry.model;

/** Represents a task without a date or time requirement. */
public final class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description the user-visible todo description.
     */
    public Todo(final String description) {
        super(description);
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }

    @Override
    public String toSaveFormat() {
        return "T | " + encodeCommonFields();
    }
}
