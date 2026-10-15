package billy.task;

/**
 * Represents a task with no date or time attached to it.
 *
 * <p>A todo adds nothing to {@link Task} beyond its {@code [T]} label, e.g.
 * {@code [T][ ] borrow book}.
 */
public class Todo extends Task {

    /** Marks a saved line as a todo. */
    public static final String TYPE_LETTER = "T";

    /** How many fields a saved todo has: the letter, the flag and the description. */
    public static final int FIELD_COUNT = 3;

    /**
     * Creates a todo that is not done yet.
     *
     * @param description what the user wants to do
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo as one line of the save file, e.g. {@code T | 0 | borrow book}. */
    @Override
    public String toSaveFormat() {
        return TYPE_LETTER + FIELD_SEPARATOR + super.toSaveFormat();
    }

    /** Returns this todo as it is shown, e.g. {@code [T][ ] borrow book}. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
