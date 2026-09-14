package stuart.task;

/**
 * Represents a task with no date/time attached to it.
 */
// ToDos class built by Claude Code, verified by myself
public class ToDos extends Task {

    /**
     * Creates a new to-do task.
     *
     * @param description what the to-do is
     */
    public ToDos(String description) {
        super(description, TaskType.TODO);
    }
}
