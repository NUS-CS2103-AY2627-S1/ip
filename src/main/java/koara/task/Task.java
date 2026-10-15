package koara.task;

import java.util.Objects;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final TaskType taskType;
    private final String description;
    private final String additionalInformation;
    private boolean isDone;

    /**
     * Creates an incomplete task with its display type and optional timing information.
     *
     * @param taskType Type of the task.
     * @param description Description of the task.
     * @param additionalInformation Additional information displayed after the description.
     */
    public Task(TaskType taskType, String description, String additionalInformation) {
        assert taskType != null : "Task type must not be null";
        assert description != null && !description.isBlank() : "Task description must not be blank";
        assert additionalInformation != null : "Additional task information must not be null";
        this.taskType = taskType;
        this.description = description;
        this.additionalInformation = additionalInformation;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the icon used to display this task's completion status.
     *
     * @return Completion status icon.
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns whether this task is completed.
     *
     * @return True when the task is marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task's description contains the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return True if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Search keyword must not be blank";
        return description.contains(keyword);
    }

    /**
     * Returns whether another task has the same type, description, and timing details.
     * Completion status is deliberately ignored when detecting duplicate tasks.
     *
     * @param other Other task to compare.
     * @return True when both tasks describe the same work.
     */
    public boolean hasSameDetails(Task other) {
        assert other != null : "Task to compare must not be null";
        return taskType == other.taskType
                && description.equals(other.description)
                && Objects.equals(additionalInformation, other.additionalInformation);
    }

    /**
     * Returns this task in the line-based format used by the data file.
     *
     * @return Serialized task data.
     */
    public String toDataString() {
        return taskType.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns this task in its user-facing display format.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        return "[" + taskType.getIcon() + "][" + getStatusIcon() + "] " + description + additionalInformation;
    }
}
