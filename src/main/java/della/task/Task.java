package della.task;

import java.util.Objects;

/**
 * Represents a task with a name and completion status.
 */
public abstract class Task {
    /**
     * Stores the description of this task.
     */
    protected String name;

    /**
     * Stores whether this task is complete.
     */
    protected boolean isDone = false;

    /**
     * Creates an incomplete task with the specified name.
     *
     * @param name Description of the task.
     */
    public Task(String name) {
        assert name != null && !name.isBlank() : "Task name must be non-null and non-blank";
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Marks this task as complete.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns this task in a format suitable for storage.
     *
     * @return Storage representation of this task.
     */
    public abstract String formatForStorage();

    /**
     * Returns a display representation of this task and its completion status.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        if (isDone) {
            return String.format("[X] %s", this.name);
        } else {
            return String.format("[ ] %s", this.name);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Task task = (Task) other;
        return name.equals(task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), name);
    }
}
