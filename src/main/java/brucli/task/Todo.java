package brucli.task;

/**
 * Represents an undated task.
 */
public class Todo extends Task {

    /**
     * Creates a todo with its internal ID and description.
     */
    public Todo(int id, String description) {
        super(id, description);
    }

    @Override
    protected String getType() {
        return "T";
    }

    @Override
    public String serialize() {
        return String.format("T | %d | %s", isDone ? 1 : 0, description);
    }
}
