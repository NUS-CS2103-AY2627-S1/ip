/**
 * Represents a task with no date or time attached.
 */
public class Todo extends Task {
    /**
     * Creates a ToDo with the supplied description.
     *
     * @param description text describing the ToDo
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeCode() {
        return "T";
    }

    @Override
    protected String getDetails() {
        return "";
    }
}
