package lebron.task;

/**
 * Represents a Todo, a Task with no associated date/time.
 */
public class Todo extends Task {
    /**
     * Constructs a Todo with the given description.
     *
     * @param description Task description.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String reformat() {
        return "T | " + super.reformat();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
