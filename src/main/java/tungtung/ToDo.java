package tungtung;

/** Represents a task without a date or time constraint. */
public class ToDo extends Task {
    /** Creates a todo task with the supplied description. */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
