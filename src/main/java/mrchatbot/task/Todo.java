package mrchatbot.task;

/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task that is not marked as done yet.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo in the display format used by the chatbot.
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description;
    }
}
