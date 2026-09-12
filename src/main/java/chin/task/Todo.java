package chin.task;

/** A plain to-do task with only a description, rendered as {@code [T][ ] desc}. */
public class Todo extends Task {
    /** Creates a to-do with the given description. */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String serialize() {
        return "T | " + super.serialize();
    }
}
