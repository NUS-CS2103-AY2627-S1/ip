package benjamin.task;

/** A task with nothing but a description, and no date attached to it. */
public class Todo extends Task {
    /** The letter that marks a todo in the save file. */
    public static final String TYPE_LETTER = "T";

    /**
     * Creates a todo that is not done yet.
     *
     * @param description what the user wants to be reminded of.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns the save line, marked with the letter {@code T}. */
    @Override
    public String toSaveFormat() {
        return TYPE_LETTER + " | " + super.toSaveFormat() + getTagsField();
    }

    /** Returns the todo as shown to the user, such as {@code [T][ ] read book}. */
    @Override
    public String toString() {
        return "[T]" + super.toString() + getTagSuffix();
    }
}
