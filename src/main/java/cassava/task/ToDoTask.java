package cassava.task;

/**
 * Represents a ToDo-type task.
 */
public class ToDoTask extends Task {

    /**
     * Default constructor for ToDoTask.
     * @param description Text description of task.
     * @param completed Completion status.
     * @param taskPriority Priority of task.
     */
    public ToDoTask(String description, boolean completed, TaskPriority taskPriority) {
        super(description, completed, taskPriority);
    }

    /**
     * Creates a ToDoTask with completed = false,
     * and taskPriority = UNKNOWN.
     * @param description Text description of task.
     */
    public ToDoTask(String description) {
        super(description);
    }

    @Override
    public String getTaskTypeToken() {
        return "T";
    }
}
