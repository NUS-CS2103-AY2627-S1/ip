package bobo.task;

/**
 * Represents a Todo task.
 */
public class Todo extends Task {

    /**
     * Constructs a Todo task with description.
     *
     * @param description Task description.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toTextLine() {
        return "T | " + super.toTextLine();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
