package joebiden.task;

/**
 * Represents a Task
 */
public class Task {
    private final String name;
    private boolean isDone;
    private String type;
    /**
     * Creates a task with the given name.
     *
     * @param name Name of the task.
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }
    /**
     * Returns whether the task is completed.
     *
     * @return True if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }
    /**
     * Marks the task as done.
     *
     * @return Message confirming the task was marked done.
     */
    public String markDone() {
        isDone = true;
        return "Nice! I've marked this task as done:\n"
                + this.toString()
                + "\n";
    }
    /**
     * Marks the task as not done.
     *
     * @return Message confirming the task was unmarked.
     */
    public String unmark() {
        isDone = false;
        return "OK, I've marked this task as not done yet:\n"
                + this.toString()
                + "\n";
    }
    /**
     * Returns the task name.
     *
     * @return Name of the task.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + name;
    }
}
