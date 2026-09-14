package morgan.task;

/**
 * Represents a basic task without specific date or time deadline.
 */
public class ToDo extends Task {
    /**
     * Constructs a ToDo task with specified description.
     *
     * @param name The textual description of the todo task.
     */
    public ToDo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
