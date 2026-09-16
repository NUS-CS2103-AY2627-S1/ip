package puyo.task;

/**
 * Represents a todo task without any specific deadline or time range.
 */
public class ToDo extends Task {

    /**
     * Constructs a {@code ToDo} task with the specified description.
     *
     * @param description The detailed description of the task.
     */
    public ToDo(String description) {
        super(description, TaskType.TODO);
    }
}
