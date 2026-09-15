package monkey.model;

/** Represents a task without a date or time attached to it. */
public class ToDos extends Task {
    /** Creates an incomplete todo with the given description. */
    public ToDos(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}