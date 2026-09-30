package kong.task;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private String description;
    private boolean isDone = false;

    /**
     * Creates an incomplete task.
     *
     * @param desc non-blank task description validated by the caller
     */
    public Task(String desc) {
        this(desc, false);
    }

    /**
     * Creates a task with an explicit completion status.
     *
     * @param desc non-blank task description validated by the caller
     * @param isDone whether the task is complete
     */
    public Task(String desc, boolean isDone) {
        assert desc != null && !desc.isBlank() : "A task description must be validated before task creation";
        assert !desc.contains("|") : "A task description cannot contain '|'";
        assert !desc.contains("\n") && !desc.contains("\r") : "A task description cannot contain newline characters";
        this.description = desc;
        this.isDone = isDone;
    }

    /** Marks this task as complete. */
    public void mark() {
        this.isDone = true;
    }
    /** Marks this task as incomplete. */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the task description for display or storage formatting.
     *
     * @return task description
     */
    protected String getDescription() {
        return this.description;
    }

    /**
     * Returns the completion status in the format used by the data file.
     *
     * @return {@code "1"} when complete; otherwise {@code "0"}
     */
    protected String getDoneStatus() {
        return this.isDone ? "1" : "0";
    }

    /**
     * Formats this task for persistent storage.
     *
     * @return pipe-delimited task data
     */
    public String toFileString() {
        return String.format("? | %s | %s", this.getDoneStatus(), this.description);
    }

    /**
     * Indicates whether this task occurs on a date.
     *
     * @param date date to test
     * @return {@code false} for a task without a date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date used when sorting tasks chronologically.
     *
     * @return an empty value because a basic task has no date
     */
    public Optional<LocalDate> getSortDate() {
        return Optional.empty();
    }

    /**
     * Checks whether another task has the same identity and details as this task.
     *
     * @param other task to compare against
     * @return {@code true} if both tasks represent the same task
     */
    public boolean isSameTask(Task other) {
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        return this.description.trim().equalsIgnoreCase(other.description.trim());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        Task other = (Task) obj;
        return this.isDone == other.isDone
                && this.description.trim().equalsIgnoreCase(other.description.trim());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), this.description.trim().toLowerCase(), this.isDone);
    }

    /**
     * Formats the task for display to the user.
     *
     * @return completion marker followed by the task description
     */
    @Override
    public String toString() {
        String out = "";
        if (this.isDone) {
            out += "[X]";
        } else {
            out += "[ ]";
        }
        return String.format("%s %s", out, this.description);
    }
}
