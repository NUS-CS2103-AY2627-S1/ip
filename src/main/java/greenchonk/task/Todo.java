package greenchonk.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description the todo description.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public Todo withDescription(String description) {
        Todo editedTodo = new Todo(description);
        copyStatusTo(editedTodo);
        return editedTodo;
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }
}
