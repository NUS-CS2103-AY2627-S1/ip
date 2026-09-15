package jaku.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description the text describing the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo with its task-type icon.
     *
     * @return the todo formatted for display by Jaku
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
