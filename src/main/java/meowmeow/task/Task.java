package meowmeow.task;

import java.time.LocalDate;

/**
 * A single task in Meowmeow's list, with a description, a {@link TaskType},
 * a {@link TaskStatus} and a {@link TaskPriority}. Subclasses ({@link Todo},
 * {@link Deadline}, {@link Event}) pass their own {@code TaskType} to the
 * constructor and add their own detail fields, overriding {@link #details()}
 * and {@link #fileDetails()} to contribute those details - {@code Task}
 * itself is never stored directly, only via its subclasses.
 */
public class Task {
    protected String description;
    protected final TaskType type;
    protected TaskStatus status;
    protected TaskPriority priority;

    /**
     * Constructs a task with the given description and type, initially not
     * done and with no priority set.
     *
     * @param description the task text as the user typed it.
     * @param type        the kind of task (todo, deadline or event).
     */
    public Task(String description, TaskType type) {
        // Parser rejects an empty command line before building a task, and every
        // subclass passes one of the TaskType constants, so a half-formed task
        // should never reach this constructor.
        assert description != null : "a task description should never be null";
        assert type != null : "every task must have a type";

        this.description = description;
        this.type = type;
        this.status = TaskStatus.NOT_DONE;
        this.priority = TaskPriority.NONE;
    }

    /** Returns the task's description text. */
    public String getDescription() {
        return description;
    }

    /** Returns whether the task is currently done or not done. */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the task's done/not-done status.
     *
     * @param status the new status.
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /** Returns the task's current priority, or {@link TaskPriority#NONE} if none has been set. */
    public TaskPriority getPriority() {
        return priority;
    }

    /**
     * Sets the task's priority.
     *
     * @param priority the new priority, or {@link TaskPriority#NONE} to clear it.
     */
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * Returns whether this task happens on the given calendar date, used by
     * the "list &lt;date&gt;" filter. A plain {@link Todo} has no date, so the
     * base answer is {@code false}; {@link Deadline} and {@link Event}
     * override this with their own date logic.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the on-screen detail this task's subclass contributes, e.g.
     * a deadline's " (by: ...)". The base task has none.
     */
    protected String details() {
        return "";
    }

    /**
     * Returns the saved-file detail this task's subclass contributes, e.g.
     * a deadline's " | 2019-12-02 1800". The base task has none.
     */
    protected String fileDetails() {
        return "";
    }

    @Override
    public String toString() {
        return "[" + type.getTag() + "][" + status.getIcon() + "] " + description + details() + prioritySuffix();
    }

    /**
     * Renders the task as one line for the saved data file, e.g.
     * {@code "T | 1 | read book"}. Subclasses with extra fields
     * ({@link Deadline}, {@link Event}) contribute their own pipe-separated
     * parts via {@link #fileDetails()}. Kept separate from {@link #toString()}
     * so the on-screen format and the on-disk format can change independently.
     */
    public String toFileString() {
        return type.getTag() + " | " + status.getFileFlag() + " | " + description + fileDetails() + priorityField();
    }

    /**
     * Returns the on-screen priority suffix, e.g. " (priority: HIGH)", or an
     * empty string when no priority is set - so an unprioritised task renders
     * exactly as it did before priorities existed.
     */
    private String prioritySuffix() {
        return priority.isSet() ? " (priority: " + priority.getLabel() + ")" : "";
    }

    /**
     * Returns the saved-file priority field, e.g. " | HIGH", or an empty
     * string when no priority is set - so an unprioritised task's save line
     * is byte-identical to what it was before priorities existed.
     */
    private String priorityField() {
        return priority.isSet() ? " | " + priority.getLabel() : "";
    }
}
