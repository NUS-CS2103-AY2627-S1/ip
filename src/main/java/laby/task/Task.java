package laby.task;

import laby.Parser;

/** Represents a task description and whether the task is complete. */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description and completion state.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is complete.
     */
    public Task(String description, boolean isDone) {
        assert Parser.isValidInput(description) : "description cannot be empty or contain separators";

        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns the task in the format shown in the console.
     */
    @Override
    public String toString() {
        return "[" + (this.isDone ? "X" : " ") + "] " + this.description;
    }

    /**
     * Sets the task's isDone.
     *
     * @param isDone Whether the task is done.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns the task in the format used for persistence.
     *
     * @return Serialized representation of the task.
     */
    public String toFileString() {
        return (this.isDone ? "1" + Parser.FIELD_SEPARATOR : "0" + Parser.FIELD_SEPARATOR)
                + this.description;
    }

    public String getDescription() {
        return this.description;
    }
}
