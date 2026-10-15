package squirtlebot.task;

/**
 * Represents the ToDo task that users can add to their list of tasks.
 * Contains a task description for the task.
 */
public class ToDo extends Task {

    /**
     * Constructs a new ToDo task.
     *
     * @param taskDescription description of the ToDo task.
     */
    public ToDo(String taskDescription) {
        super(taskDescription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof ToDo otherToDo) {
            return super.equals(otherToDo);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("[T] %s", super.toString());
    }
}
