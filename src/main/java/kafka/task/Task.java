package kafka.task;

/**
 * Represents a task with a description and completion state.
 */
public abstract class Task {
    private String name;
    private boolean isDone;

    /**
     * Creates an unfinished task with the supplied name.
     *
     * @param name Description of the work to remember.
     */
    protected Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Copies the description and completion state without sharing mutable task state.
     */
    protected Task(Task source) {
        this.name = source.name;
        this.isDone = source.isDone;
    }

    /**
     * Returns an independent copy with the same type, description, status, and schedule.
     */
    abstract Task copy();

    /**
     * Marks this task as completed.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Builds the common checkbox and name shown for every task type.
     *
     * @return Checkbox followed by this task's name.
     */
    public String display() {
        String status = isDone ? "[X]" : "[ ]";
        return status + " " + name;
    }

    /**
     * Builds the storage fields shared by every concrete task type.
     *
     * @param taskType One-letter code identifying the concrete task type.
     * @return Type, completion state, and name in the storage format.
     */
    protected String toDataString(String taskType) {
        String status = isDone ? "1" : "0";
        return taskType + " | " + status + " | " + name;
    }

    /**
     * Converts this task into one line in the task data file.
     *
     * @return Complete serialized form of this task.
     */
    public abstract String toDataString();

    /**
     * Replaces this task's name.
     *
     * @param newName New name for this task.
     */
    void rename(String newName) {
        name = newName;
    }
}
