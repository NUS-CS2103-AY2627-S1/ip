package bot67.task;

/**
 * Represents a task without a deadline or event time.
 */
public class Todo extends Task {

    /**
     * Creates a ToDo task from a user command.
     *
     * @param command the complete ToDo command
     */
    public Todo(String command) {
        super(command.substring(5), TaskType.TODO);
    }

    /** Returns the standard ToDo task description. */
    @Override
    public String getDescription() {
        return super.getDescription();
    }
}
