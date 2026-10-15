package hu9o.task;

/**
 * Represents a regular todo task.
 */
public class ToDoTask extends Task {
    /**
     * Creates an unfinished todo task.
     *
     * @param description the task's description.
     */
    public ToDoTask(String description) {
        super(description, "T");
    }

    @Override
    protected String specificationSuffix() {
        return "";
    }
}
