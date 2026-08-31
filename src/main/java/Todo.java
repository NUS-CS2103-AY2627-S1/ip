/**
 * Represents a task with no date or time attached.
 */
public class Todo extends Task {
    /**
     * Creates a ToDo with the supplied description.
     *
     * @param description text describing the ToDo
     * @throws BibiException if the description is blank
     */
    public Todo(String description) throws BibiException {
        super(requireDescription(description, "Use todo followed by a description."));
    }

    @Override
    protected String getTypeCode() {
        return "T";
    }

    @Override
    protected String getDetails() {
        return "";
    }

    /**
     * Returns no extra fields because a ToDo stores only its description.
     */
    @Override
    protected String getSaveFields() {
        return "";
    }
}
