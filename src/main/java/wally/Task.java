package wally;

import java.time.LocalDateTime;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String name;
    private boolean isCompleted;

    /**
     * Creates a task with the specified name and an incomplete status.
     *
     * @param name Name of the task.
     */
    public Task(String name) {
        this.name = name;
        this.isCompleted = false;
    }

    /**
     * Updates this task's completion status.
     *
     * @param isCompleted Whether this task is completed.
     */
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    /** Returns whether this task is completed. */
    public boolean isCompleted() {
        return isCompleted;
    }

    /** Returns the task description without its type, status, or dates. */
    public String getDescription() {
        return name;
    }

    @Override
    public String toString() {
        return "[" + (isCompleted ? "X" : " ") + "] " + name;
    }

    /**
     * Returns the command used to create this task.
     *
     * @return Command that creates this task.
     */
    public String getCommand() {
        return name;
    }

    /**
     * Returns the date-time used to place this task in the schedule.
     * To-do tasks have no date-time and are therefore listed after dated tasks.
     *
     * @return The task's schedule date-time, or {@code null} when it has none.
     */
    public LocalDateTime getScheduleDateTime() {
        return null;
    }
}
