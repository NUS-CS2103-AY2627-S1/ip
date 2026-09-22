package nico;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description
     *
     * @param description Task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the format used for display and storage.
     *
     * @return Formatted todo text, including its completion status.
     */
    @Override
    public String toString() {
        return "[T]" + getStatusMarker() + " " + getDescription();
    }
}
