package margit.task;

/** Represents a task without a date or time. */
public class TodoTask extends Task {
    /** Creates a todo task with the given description. */
    public TodoTask(String description) {
        super(description);
    }

    /** Returns the user-facing representation with the todo type marker. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
