package anniechat.task;

/** Represents a task without a deadline or event time. */
public class ToDo extends Task {
    String taskDesc;

    /**
     * Creates a to-do task from a command such as {@code todo read book}.
     *
     * @param desc command containing the to-do description.
     */
    public ToDo(String desc) {
        super(desc);
        taskDesc = desc.substring(5);
    }

    @Override
    public String getTaskDesc() {
        return taskDesc;
    }

    @Override
    public String getTaskIcon() {
        return "T";
    }

    @Override
    public String toSaveFormat() {
        return this.getTaskIcon() + " | " + (isDone ? "1" : "0") + " | " + this.taskDesc;
    }
}
