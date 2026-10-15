package doge.model;

/** Represents a task without a deadline or event time. */
public class Todo extends Task {

    /** Creates an incomplete todo with the given description. */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo in the display format for todo tasks. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
