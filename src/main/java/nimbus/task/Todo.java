package nimbus.task;

/** Represents a task without an associated date or time. */
public class Todo extends Task {
    /** Creates a todo task. */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
