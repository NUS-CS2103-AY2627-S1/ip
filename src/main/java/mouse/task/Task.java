package mouse.task;

/**
 * A task with a description and done status.
 * Base type for to-dos, deadlines, and events.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;
    private Priority priority;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.priority = Priority.NONE;
    }

    /**
     * Returns {@code X} if done, otherwise a space.
     *
     * @return Status icon character as a string.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return Description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is marked done.
     *
     * @return {@code true} if done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's priority.
     *
     * @return Current priority.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets this task's priority.
     *
     * @param priority Priority to assign.
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Returns whether {@code other} is the same crumb, ignoring done state and priority.
     *
     * @param other Task to compare.
     * @return {@code true} if both are the same kind of task with the same name.
     */
    public boolean isDuplicateOf(Task other) {
        return other != null
                && getClass() == other.getClass()
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns the status icon and description, for example {@code [ ] borrow book}.
     *
     * @return Display string without the task-type letter.
     */
    @Override
    public String toString() {
        String text = "[" + getStatusIcon() + "] " + description;
        if (priority == Priority.NONE) {
            return text;
        }
        return text + " (priority: " + priority.toDisplayString() + ")";
    }

    /**
     * Returns a single-line save format for this task.
     *
     * @return Encoded task line.
     */
    public abstract String encode();

    /**
     * Returns the shared save prefix: type, done flag, priority, and description.
     *
     * @param typeSymbol Task type letter.
     * @param extra Extra fields such as dates.
     * @return Encoded fields joined by {@code |}.
     */
    protected String encodeFields(String typeSymbol, String... extra) {
        StringBuilder line = new StringBuilder();
        line.append(typeSymbol)
                .append(" | ")
                .append(isDone ? "1" : "0")
                .append(" | ")
                .append(priority.toDisplayString())
                .append(" | ")
                .append(description);
        for (String field : extra) {
            line.append(" | ").append(field);
        }
        return line.toString();
    }
}
