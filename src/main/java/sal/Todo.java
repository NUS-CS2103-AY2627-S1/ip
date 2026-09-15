package sal;

/**
 * A task with no date or time attached.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description Text describing the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return withTags("[T]" + super.toString());
    }
}
