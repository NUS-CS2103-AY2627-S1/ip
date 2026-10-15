package ev.task;

/**
 * A task with nothing but a description, such as {@code todo read book}.
 */
public class Todo extends Task {

    /** One-letter type of this task in the save file. */
    public static final String TYPE = "T";

    /**
     * Creates a todo that is not done yet.
     *
     * @param description what the user typed as the description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeName() {
        return "a todo";
    }

    @Override
    public String toFileFormat() {
        return encode(TYPE);
    }

    @Override
    public String toString() {
        return "[" + TYPE + "]" + super.toString();
    }
}
