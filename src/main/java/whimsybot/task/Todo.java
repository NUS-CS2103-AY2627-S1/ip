package whimsybot.task;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the given description.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo with its task type marker. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
