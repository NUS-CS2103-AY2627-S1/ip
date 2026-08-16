/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String description;
    private boolean complete;

    /**
     * Creates an incomplete task with the provided description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.complete = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markComplete() {
        complete = true;
    }

    public void markIncomplete() {
        complete = false;
    }

    /**
     * Returns a display form that includes the task's completion status.
     *
     * @return the task description prefixed with {@code [ ]} or {@code [X]}
     */
    @Override
    public String toString() {
        return (complete ? "[X] " : "[ ] ") + description;
    }
}
