package wobble.tasks;

/** Represents one task and whether it has been completed. */
public class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;

    /** Creates an unfinished task with the given description. */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /** Creates an unfinished task with the given description and type. */
    protected Task(String description, TaskType type) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A task must have a non-empty description");
        }
        if (type == null) {
            throw new IllegalArgumentException("A task must have a task type");
        }
        assert description != null : "A task must have a description";
        assert type != null : "A task must have a task type";
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as unfinished. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the status icon used when displaying this task. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns whether this task is completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns this task's type. */
    public TaskType getType() {
        return type;
    }

    /** Returns whether another task has the same type and description as this task. */
    public boolean hasSameDetailsAs(Task other) {
        return other != null && type == other.type && description.equals(other.description);
    }

    /** Returns the common status portion of a task's display text. */
    @Override
    public String toString() {
        return getDisplayPrefix() + description;
    }

    /** Returns the common type and completion prefix used in task display text. */
    protected String getDisplayPrefix() {
        return "[" + type.getIcon() + "][" + getStatusIcon() + "] ";
    }
}
