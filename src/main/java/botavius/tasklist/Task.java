package botavius.tasklist;

import botavius.exception.BotaviusException;

/**
 * Represents a task entered by the user and its completion status.
 */
public class Task {
    /** Text describing the task. */
    private String description;
    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates a new incomplete task.
     *
     * @param description text describing the task
     * @throws BotaviusException if the description is empty
     */
    public Task(String description) {
        this.description = description.strip();
        this.isDone = false;
        if (description.length() <= 0) {
            throw new BotaviusException("description not provided.");
        }
    }

    /**
     * Updates the task's completion status.
     *
     * @param done {@code true} to mark the task complete; {@code false}
     *             to mark it incomplete
     */
    public void setDone(boolean done) {
        isDone = done;
    }

    /**
     * Returns the task description without completion or type markers.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task is complete.
     *
     * @return {@code true} when the task is marked complete
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task with a completion marker and its description.
     *
     * @return {@code [X]} followed by the description if complete, otherwise
     *         {@code [ ]} followed by the description
     */

    @Override
    public String toString() {
        String return_string = "";
        if (isDone) {
            return_string = "[X] ";
        } else {
            return_string = "[ ] ";
        }
        return return_string + this.description;
    }

    /**
     * Returns the task text in the base format used for persistence.
     *
     * @return completion marker followed by the description
     */
    public String toStorageString() {
        String return_string = "";
        if (isDone) {
            return_string = "[X] ";
        } else {
            return_string = "[ ] ";
        }
        return return_string + this.description;
    }


}
