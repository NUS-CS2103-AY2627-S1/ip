package rambo.task;

import java.time.format.DateTimeFormatter;

import rambo.exception.RamboException;

/**
 * Represents a task with a name, done status, and optional priority.
 */
public class Task {
    /** Value used when a task has not been assigned a priority. */
    public static final int NO_PRIORITY = 0;
    /** Highest priority level users can assign. */
    public static final int MIN_PRIORITY_LEVEL = 1;
    /** Lowest priority level users can assign. */
    public static final int MAX_PRIORITY_LEVEL = 3;

    protected static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final String taskName;
    private boolean isDone;
    private int priorityLevel;

    /**
     * Constructs a new task with the given name and marks it as not done.
     *
     * @param taskName the name of the task
     */
    public Task(String taskName) {
        this.taskName = taskName;
        this.isDone = false;
        this.priorityLevel = NO_PRIORITY;
    }

    /**
     * Returns the name of the task.
     *
     * @return the task name
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Returns true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task's priority level, or {@link #NO_PRIORITY} if none has been assigned.
     *
     * @return priority level from 1 to 3, or 0 when unprioritized
     */
    public int getPriorityLevel() {
        return priorityLevel;
    }

    /**
     * Returns whether a priority has been assigned to this task.
     *
     * @return true if this task has a priority
     */
    public boolean hasPriority() {
        return priorityLevel != NO_PRIORITY;
    }

    /**
     * Assigns a priority level to this task.
     *
     * @param priorityLevel priority level from 1 (highest) to 3 (lowest)
     * @throws RamboException if the priority level is outside the supported range
     */
    public void setPriorityLevel(int priorityLevel) throws RamboException {
        validatePriorityLevel(priorityLevel);
        this.priorityLevel = priorityLevel;
    }

    /**
     * Validates the priority range shared by task updates and user-input parsing.
     *
     * @param priorityLevel priority level to validate
     * @throws RamboException if the level is outside the assignable range
     */
    public static void validatePriorityLevel(int priorityLevel) throws RamboException {
        if (priorityLevel < MIN_PRIORITY_LEVEL || priorityLevel > MAX_PRIORITY_LEVEL) {
            throw new RamboException("Priority level must be between 1 and 3!");
        }
    }

    /**
     * Toggles the done status of the task and returns the new status.
     *
     * @return the new done status
     */
    public boolean toggleDone() {
        isDone = !isDone;
        return isDone;
    }

    /**
     * Converts the task into the required string format for storage.
     *
     * @return the data string representation of the task
     */
    public String toDataString() {
        return String.format("T|%s|%s|%d", isDone ? "X" : "", taskName, priorityLevel);
    }

    /**
     * Returns a string representation of the task suitable for display.
     *
     * @return the task string representation
     */
    @Override
    public String toString() {
        return String.format("[T][%s]%s %s", isDone ? "X" : "", getPriorityTag(), taskName);
    }

    /**
     * Returns the priority marker used in task displays, or an empty string for unprioritized tasks.
     *
     * @return display-ready priority marker
     */
    protected String getPriorityTag() {
        return hasPriority() ? String.format("[P%d]", priorityLevel) : "";
    }
}
