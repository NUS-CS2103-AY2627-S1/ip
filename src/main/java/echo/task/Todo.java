package echo.task;

/**
 * Represents a task without any date attached to it.
 */
public class Todo extends Task {
    private static final TaskType TYPE = TaskType.TODO;
    /**
     * Creates an incomplete {@code todo} with the given description.
     *
     * @param description text that describes the {@code todo}
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates {@link String} representation of the {@link Todo} instance.
     * @return String representation of {@link Todo}
     */
    @Override
    public String toString() {
        return "[" + TYPE.toString() + "]" + super.toString();
    }

    /**
     * Creates save {@link String} representation of the {@link Todo} instance.
     * @return String representation of {@link Todo}
     */
    @Override
    public String toSaveFormat() {
        return TYPE.toString() + " | " + super.toSaveFormat();
    }

    /**
     * Reconstructs a {@code todo} from its save-format fields (type letter,
     * completion date-time, description).
     *
     * @param fields the split save-file line for this {@code todo}.
     * @return the reconstructed {@code todo}.
     */
    public static Todo fromSaveFormat(String[] fields) {
        if (fields.length != 3) {
            throw new IllegalArgumentException("A saved todo must have exactly 3 fields.");
        }
        return new Todo(fields[2]);
    }

    @Override
    public TaskType getTaskType() {
        return TYPE;
    }
}
