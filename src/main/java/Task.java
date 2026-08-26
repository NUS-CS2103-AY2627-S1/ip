/**
 * Represents a task that can be marked as complete or incomplete.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing whether this task is complete.
     *
     * @return {@code X} if the task is complete, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return {@code true} if the task is done, {@code false} otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the description of this task.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the single-letter code identifying this task's type in the save file.
     * Subclasses with their own type (e.g. {@link Deadline}, {@link Event}) override this.
     *
     * @return {@code "T"}, the type code for a plain task.
     */
    protected String getTypeCode() {
        return "T";
    }

    /**
     * Returns this task's representation for the save file, using the pipe-delimited
     * format {@code <type> | <done flag> | <description>}. Subclasses with extra details
     * (such as a deadline date) override this to append their own fields.
     *
     * @return the line to write to the data file for this task.
     */
    public String toSaveFormat() {
        return getTypeCode() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
