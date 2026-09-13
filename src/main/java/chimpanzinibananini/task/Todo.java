package chimpanzinibananini.task;

/**
 * Represents a task without a deadline or scheduled period.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete to-do task with the specified description.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }
}
