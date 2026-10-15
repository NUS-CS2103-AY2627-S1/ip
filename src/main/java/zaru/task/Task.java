package zaru.task;

import zaru.exception.ZaruException;

/** Stores the common description and completion state of a task. */
public abstract class Task implements Comparable<Task> {
    private final String description;
    private boolean isCompleted;

    /**
     * Creates a task with its description and completion state.
     *
     * @param description Task description.
     * @param isCompleted Whether the task is already complete.
     */
    public Task(String description, boolean isCompleted) {
        assert description != null : "Task description should have been validated before construction.";

        this.description = description;
        this.isCompleted = isCompleted;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether the task is complete.
     *
     * @return {@code true} if the task is complete.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Updates the task completion state.
     *
     * @param isCompleted New completion state.
     */
    public void setCompleted(boolean isCompleted) throws ZaruException {
        if (isCompleted == this.isCompleted) {
            if (isCompleted) {
                throw new ZaruException("This task is already marked as complete.");
            }
            throw new ZaruException("This task is already unmarked.");
        }
        this.isCompleted = isCompleted;
    }

    /**
     * Checks whether another task has the same type and identifying details.
     * Completion state is intentionally ignored so completing a task does not make it a distinct task.
     *
     * @param other Task to compare against.
     * @return {@code true} if both tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns the common completion marker and task description.
     *
     * @return Formatted task text.
     */
    @Override
    public String toString() {
        return String.format("%s %s", isCompleted ? "[x]" : "[ ]", description);
    }

    @Override
    public int compareTo(Task other) {
        int typeComparison = Integer.compare(getTypePriority(), other.getTypePriority());
        if (typeComparison != 0) {
            return typeComparison;
        }

        return compareWithinType(other);
    }

    /**
     * Returns the priority used to group tasks by type when sorting.
     *
     * @return Type priority, where a lower value appears first.
     */
    protected abstract int getTypePriority();

    /**
     * Compares two tasks of the same type by their descriptions.
     *
     * @param other Other task of the same type.
     * @return A negative value, zero, or a positive value according to the ordering.
     */
    protected int compareWithinType(Task other) {
        return description.compareToIgnoreCase(other.description);
    }
}
