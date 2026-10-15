package ace.task;

/**
 * Represents a single task in the user's task list. This class is abstract so that
 * a bare Task can never be instantiated directly — every task must be one of the
 * concrete subtypes (Todo, Deadline, Event), each of which knows how to display and
 * persist itself.
 */
public abstract class Task {
    private final String description;
    private boolean isCompleted;

    /**
     * Creates a new, incomplete task with the given description.
     *
     * @param description the text describing what this task is.
     */
    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    /**
     * Returns this task's description.
     *
     * @return the task's description text.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Marks this task as completed.
     */
    public void completeTask() {
        this.isCompleted = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void resetTask() {
        this.isCompleted = false;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Returns the representation of this task used when saving it to disk, in a
     * format that can be parsed back into an equivalent task on load. Subclasses
     * must implement this since each task type persists differently.
     *
     * @return the save-file representation of this task.
     */
    public abstract String loadFormat();

    /**
     * Returns the completion-status portion of this task's display representation
     * (e.g. "[X] description" or "[ ] description"). Subclasses build on this by
     * prefixing their own type marker.
     *
     * @return this task's completion status and description, formatted for display.
     */
    @Override
    public String toString() {
        if (this.isCompleted) {
            return "[X] " + this.getDescription();
        } else {
            return "[ ] " + this.getDescription();
        }
    }
}
