package jelly.model;

/**
 * Represents a basic to-do task.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete to-do task.
     *
     * @param description the text describing the to-do.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTaskType() {
        return "T";
    }
}
