package sylveon;

/** Represents a task without a deadline or event time period. */
public class Todo extends Task {
    /** Creates a todo task. */
    public Todo(String description) {
        super(description);
    }

    /** Returns the display representation of this todo task. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
