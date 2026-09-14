package nova.task;

/**
 * A task with nothing attached to it but a description, i.e. something to do
 * with no date of any kind.
 */
public class Todo extends Task {
    /**
     * Creates a todo that is not done yet.
     *
     * @param description what the user called this task
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileString() {
        return "T | " + super.toFileString();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
