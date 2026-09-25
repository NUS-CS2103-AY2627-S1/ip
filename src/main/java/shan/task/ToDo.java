package shan.task;

/**
 * Represents a task without a date or time.
 */
public class ToDo extends Task {
    /**
     * Constructs a ToDo task.
     *
     * @param taskName Name of the task.
     */
    public ToDo(String taskName) {
        super(taskName);
    }

    /**
     * Returns this ToDo in the save-file format.
     *
     * @return Serialized ToDo.
     */
    @Override
    public String toFileString() {
        return String.format("T | %s", getFileFields());
    }

    /**
     * Returns the display representation of this ToDo.
     *
     * @return Display representation of this ToDo.
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
