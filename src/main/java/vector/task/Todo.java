package vector.task;

/**
 * Represents a Todo task.
 */
public class Todo extends Task {

    /**
     * Constructs a Todo task.
     *
     * @param description The description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileFormat() {
        return "T" + super.toFileFormat();
    }
}

