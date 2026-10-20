package yapper;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo as a pipe-separated storage record.
     *
     * @return Task type, completion state, description.
     */
    @Override
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns this todo in the format used by the chatbot.
     *
     * @return Type marker, completion status, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
