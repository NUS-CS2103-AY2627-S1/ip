package computa;
/**
 * Represents a basic task without any specific date or time constraints.
 */
public class Todo {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a new Todo task with the specified description.
     * The task is marked as undone by default.
     *
     * @param description The text description of the task.
     */
    public Todo(String description) {
        this(description, false);
    }
    /**
     * Constructs a Todo task with a specified description and completion status.
     *
     * @param description The text description of the task.
     * @param isDone      The completion status of the task (true if done, false if undone).
     */
    public Todo(String description, boolean isDone) {
        if (description == null) {
            throw new IllegalArgumentException("A task description must not be null");
        }
        this.description = description;
        this.isDone = isDone;
    }
    /**
     * Toggles the completion status of the task.
     */

    public void changeStatusIcon() {
        isDone = !isDone;
    }
    /**
     * Retrieves the status icon representing whether the task is completed.
     *
     * @return A string containing "[X]" if the task is done, or "[]" if it is not.
     */

    public String getStatusIcon() {
        return isDone ? "[X]" : "[]";
    }
    /**
     * Formats the task into a readable string for the user interface.
     *
     * @return A formatted string displaying the task type, status, and description.
     */

    public String getTaskDescription() {
        return getTaskType() + getStatusIcon() + " " + description + "\n";
    }

    public String getDescription() {
        return description;
    }



    /**
     * Retrieves the specific task type identifier.
     *
     * @return A string representing the task type ("[T]" for Todo).
     */
    public String getTaskType() {
        return "[T]";
    }
    /**
     * Formats the task into a standardized string for saving to the hard drive.
     *
     * @return A formatted string separated by pipes representing the task's save state.
     */
    public String toFileFormat() {
        int completionStatus = isDone ? 1 : 0;
        return "T | " + completionStatus + " | " + description;
    }
}
