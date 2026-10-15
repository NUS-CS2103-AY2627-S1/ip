package labubu;

/**
 * Represents a task without a date or time constraint.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do task with the given title.
     *
     * @param taskTitle Title of the task.
     */
    public ToDo(String taskTitle) {
        super(taskTitle);
    }

    @Override
    public String getMarker() {
        return "T";
    }

    @Override
    public String toSaveFormat() {
        return getMarker() + "|" + getStatus().name() + "|" + taskTitle;
    }
}
