package miaow.task;

/**
 * task
 */
public class Task {
    private String taskName;
    private boolean marked;

    /**
     * task constructor
     * @param name
     */
    public Task(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Task description cannot be empty."
            );
        }
        this.taskName = name;
        this.marked = false;
    }

    /**
     * Returns marked status of task.
     */
    public boolean isDone() {
        return this.marked;
    }

    /**
     * Returns name of task.
     */
    public String getDescription() {
        return this.taskName;
    }

    /**
     * Marks a task as done.
     */
    public void mark() {
        this.marked = true;
    }

    /**
     * Marks a task as undone.
     */
    public void unmark() {
        this.marked = false;
    }

    /**
     * Returns a string of the task as stored in the file.
     */
    public String toFileFormat() {
        String statusIcon = marked ? "1" : "0";
        return "T | " + statusIcon + " | " + taskName;
    }

    /**
     * Returns a string of the task as it should be displayed in the list.
     */
    @Override
    public String toString() {
        return "[T]" + "[" + (marked ? "X" : " ") + "] " + taskName;
    }
}
