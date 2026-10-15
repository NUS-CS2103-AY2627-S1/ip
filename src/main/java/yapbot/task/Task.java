package yapbot.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private Priority priority;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Parser/Storage must validate the description before constructing a task";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the icon used to show whether this task is complete.
     *
     * @return {@code "X"} when complete; otherwise, a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the status as it should appear in the save file ("1" for done, "0" otherwise).
     *
     * @return {@code "1"} when complete; otherwise, {@code "0"}
     */
    public String getStatusValue() {
        return isDone ? "1" : "0";
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
     * Returns this task's priority, or {@code null} if none has been set.
     *
     * @return the priority, or {@code null}.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets this task's priority.
     *
     * @param priority the priority to set.
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Returns the single-letter icon identifying this task's type in the save file.
     * Subclasses override this to identify themselves (e.g. "D" for Deadline).
     *
     * @return the type icon, defaults to "T"
     */
    protected String getTypeIcon() {
        return "T";
    }

    /**
     * Returns this task serialized as a single line for the save file, in the format:
     * {@code type | status | description}. Subclasses append any extra fields they have.
     *
     * @return the file-format representation of this task
     */
    public String toFileFormat() {
        return getTypeIcon() + " | " + getStatusValue() + " | " + description;
    }

    /**
     * Appends this task's priority to an already-built file-format line, if
     * one is set. Subclasses call this last, after appending their own
     * extra fields, so the priority field (when present) is always the
     * final field on the line; this keeps every earlier field's position
     * (e.g. Deadline's 'by') unaffected by whether a priority is present.
     *
     * @param fileFormatSoFar the line built so far, without the priority field.
     * @return {@code fileFormatSoFar} unchanged if no priority is set;
     *         otherwise, with the priority appended as the final field.
     */
    protected final String appendPriorityField(String fileFormatSoFar) {
        return priority == null ? fileFormatSoFar : fileFormatSoFar + " | " + priority;
    }

    /**
     * Returns whether this task has the same user-visible details as
     * {@code other}, i.e. they would look like duplicate entries in the
     * list. The base check is the same type and description (matched
     * case-insensitively, since "Read Book" and "read book" are the same
     * task to a user); subclasses with extra fields (e.g. {@link Deadline}'s
     * date, {@link Event}'s from/to) override this to also compare those.
     *
     * @param other the task to compare against.
     * @return {@code true} if the two tasks would look like duplicates.
     */
    public boolean isDuplicateOf(Task other) {
        return other != null
                && getClass() == other.getClass()
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns this task as it should be displayed to the user, e.g.
     * {@code "[X]read book"}, or {@code "[X][HIGH]read book"} once a
     * priority is set. Nothing is shown for the priority when none is set.
     *
     * @return the display representation of this task.
     */
    @Override
    public String toString() {
        String priorityTag = priority == null ? "" : "[" + priority + "]";
        return "[" + getStatusIcon() + "]" + priorityTag + description;
    }
}
