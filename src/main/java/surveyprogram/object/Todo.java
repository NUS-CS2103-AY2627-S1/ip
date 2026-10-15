package surveyprogram.object;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Item {
    /**
     * Creates an undated task.
     *
     * @param description task description
     */
    public Todo(String description) {
        super(description);
    }

    /** Creates an undated task restored with a stable identifier. */
    public Todo(String id, String description) {
        super(id, description);
    }

    /**
     * Returns the display form of this todo, including its type and completion markers.
     *
     * @return formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
