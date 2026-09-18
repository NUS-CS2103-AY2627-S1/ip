package fein.task;

/** A task without an attached date or time. */
public class Todo extends Task {
    /** Creates an incomplete todo task. */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo with its type marker. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
