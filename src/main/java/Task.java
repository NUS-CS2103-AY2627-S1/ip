/**
 * Represents information shared by every kind of task.
 *
 * <p>Subclasses provide their own type code and any date or time details.
 */
public abstract class Task {
    private final String description;
    private boolean complete;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description text describing the task
     */
    protected Task(String description) {
        this.description = description;
        complete = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markComplete() {
        complete = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markIncomplete() {
        complete = false;
    }

    /**
     * Returns the single-letter code used to display this task type.
     *
     * @return the type code
     */
    protected abstract String getTypeCode();

    /**
     * Returns any type-specific information that follows the description.
     *
     * @return displayable details, including any leading spacing
     */
    protected abstract String getDetails();

    /**
     * Returns a display form containing the task type, status, and details.
     *
     * @return the formatted task
     */
    @Override
    public String toString() {
        return "[" + getTypeCode() + "]"
                + (complete ? "[X] " : "[ ] ")
                + description
                + getDetails();
    }
}
