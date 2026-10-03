package ramly.model;

import java.util.Locale;

import ramly.validation.InputValidator;

/** Represents a task with a description, type, and completion state. */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /** Creates an incomplete task with the given description and type. */
    public Task(String description, TaskType type) {
        assert description != null : "Task description must not be null";
        assert type != null : "Task type must not be null";
        this.description = InputValidator.normalizeDescription(description);
        this.isDone = false;
        this.type = type;
    }

    /** Returns this task's type. */
    public TaskType getType() {
        return this.type;
    }

    /** Returns whether another task has the same normalized identifying fields. */
    public boolean hasSameIdentity(Task other) {
        if (other == null || type != other.type) {
            return false;
        }
        return normalizedDescription().equals(other.normalizedDescription());
    }

    /** Returns the normalized description used when detecting duplicates. */
    protected String normalizedDescription() {
        return description.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").strip();
    }

    /** Returns the display icon corresponding to the completion state. */
    public String getStatusIcon() {
        return isDone ? "[✓]" : "[ ]";
    }

    /** Marks this task as completed. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        this.isDone = false;
    }

    /** Returns the serialized representation used by storage. */
    public String toFileString() {
        return type.getCode() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /** Returns the user-facing representation of this task. */
    @Override
    public String toString() {
        return (type.getIcon() + getStatusIcon() + " " + this.description);
    }
}
