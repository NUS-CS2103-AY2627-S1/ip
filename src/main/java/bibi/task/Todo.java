package bibi.task;

import bibi.BibiException;

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
        super(requireTaskText(description, "Use todo followed by a description."));
    }

    /**
     * Returns the code T, which marks a ToDo in the list and in the save file.
     */
    @Override
    protected String getTypeCode() {
        return "T";
    }

    /**
     * Returns no details, because a ToDo carries no date or time.
     */
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
