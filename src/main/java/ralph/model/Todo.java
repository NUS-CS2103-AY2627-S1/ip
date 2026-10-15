package ralph.model;

/**
 * A ToDo task: no date/time attached.
 */
public class Todo extends Task {
    /**
     * Constructs a Todo task with the given description.
     *
     * @param description the todo description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a display string for the todo task.
     *
     * @return formatted todo string
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
