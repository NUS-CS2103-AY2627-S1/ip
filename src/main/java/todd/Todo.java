package todd;

/** Represents a task without an associated date or time. */
public class Todo extends Task {
    /** Creates an incomplete todo with the specified description. */
    public Todo(String description) {
        super(description);
    }

    /** Returns a display-ready representation of this todo. */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + getDescription();
    }
}
