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
     * @throws BibiException if the description is blank
     */
    protected Task(String description) throws BibiException {
        if (description == null || description.isBlank()) {
            throw new BibiException("A task needs a description.");
        }
        this.description = description;
        complete = false;
    }

    /**
     * Checks a task description before it is stored in a subclass.
     *
     * @param description text supplied as the task description
     * @param errorMessage explanation to show when the description is missing
     * @return the supplied description when it is valid
     * @throws BibiException if the description is blank
     */
    protected static String requireDescription(String description, String errorMessage)
            throws BibiException {
        if (description == null || description.isBlank()) {
            throw new BibiException(errorMessage);
        }
        return description;
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
