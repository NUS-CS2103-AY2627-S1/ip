package trybot.task;

/**
 * A task without an attached date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo task.
     *
     * @param description text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Formats this task with the todo type icon and completion status.
     *
     * @return formatted todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
