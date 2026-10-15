package walle.task;

/**
 * Base class for every task WALLE tracks (see {@link ToDo}, {@link Deadline},
 * {@link Event}), holding the task's description and completion status.
 */
public abstract class Task {

    private String task;
    private boolean isDone;

    /**
     * Creates a new, not-yet-done task with the given description.
     *
     * @param task the task's description.
     */
    public Task(String task) {
        assert task != null && !task.isBlank() : "Task description should never be null/blank; "
                + "callers (Parser, Storage) are expected to have already rejected that input.";
        this.task = task;
        this.isDone = false;
    }

    /**
     * Creates a task with the given description and completion status, e.g.
     * when restoring a task previously loaded from storage.
     *
     * @param task   the task's description.
     * @param isDone whether the task is already marked done.
     */
    public Task(String task, boolean isDone) {
        assert task != null && !task.isBlank() : "Task description should never be null/blank; "
                + "callers (Parser, Storage) are expected to have already rejected that input.";
        this.task = task;
        this.isDone = isDone;
    }

    /**
     * Marks this task as done.
     */
    public void check() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void uncheck() {
        this.isDone = false;
    }

    /**
     * Returns whether this task is marked done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isChecked() {
        return this.isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return the task's description.
     */
    public String getTask() {
        return this.task;
    }

    /**
     * Returns this task's line format for the memory file, e.g. {@code T;X;task description}.
     *
     * @return the task's line format for the memory file.
     */
    public abstract String getMemoryFormat();

    private String getCheckbox() {
        return this.isDone ? "[X]" : "[ ]";
    }

    @Override
    public String toString() {
        return this.getCheckbox() + " " + this.task;
    }

}
