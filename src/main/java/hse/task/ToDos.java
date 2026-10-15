package hse.task;

/** Represents a task without a deadline or time range. */
public class ToDos extends Task {
    /** Creates a todo with the supplied description. */
    public ToDos(String description) {
        super(description);
    }

    /** Returns this todo in the standard todo display format. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
