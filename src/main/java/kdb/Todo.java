package kdb;

/** Represents a simple task without a deadline or event period. */
public class Todo extends Task {
    /**
     * Creates an unfinished todo task.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /** Creates a todo with a priority. */
    public Todo(String description, Priority priority) {
        super(description, priority);
    }

    /** Returns a human-readable representation of this todo. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
