package anby.task;

/**
 * Represents a task tracked by Anby.
 */
public class Task {
    private String name;
    private boolean isDone;

    /**
     * Creates a task with the given name.
     *
     * @param name task description
     */
    public Task(String name) {
        this.name = name;
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
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Checks whether this task is done.
     *
     * @return true if this task is done
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the task name.
     *
     * @return task name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the done state in the storage format.
     *
     * @return 1 if this task is done, otherwise 0
     */
    public int getDoneStatusAsInt() {
        return this.isDone ? 1 : 0;
    }

    /**
     * Converts this task into the format used in the data file.
     *
     * @return file storage representation of this task
     */
    public String toFileString() {
        return this.getDoneStatusAsInt() + " | " + this.getName();
    }

    /**
     * Returns the user-facing representation of this task.
     *
     * @return display string for this task
     */
    @Override
    public String toString() {
        if (this.isDone) {
            return "[X] " + this.name;
        } else {
            return "[ ] " + this.name;
        }
    }
}
