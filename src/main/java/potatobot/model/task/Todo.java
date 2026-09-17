package potatobot.model.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an undated task with the specified description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Formats this task with its todo type label.
     *
     * @return Task description and todo type label.
     */
    @Override
    public String toString() {
        return super.toString() + " (Todo)";
    }
}
