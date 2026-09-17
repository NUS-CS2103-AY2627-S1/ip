package ada;

/**
 * Represents a general‑purpose task storing its description and completion status.
 * Serves as the base class for {@code Todo}, {@code Deadline} and {@code Event}.
 * Provides common operations to mark task as done or undone, and formats output
 * for console display and file persistence.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates a new Task. The task is initially unfinished.
     *
     * @param description the textual content describing the task
     */
    public Task(String description) {
        assert description != null : "A task must have a non-null description";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Checks whether this task is marked as completed.
     *
     * @return true if task is done, false otherwise
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Sets this task’s status to complete.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Sets this task’s status to incomplete.
     */
    public void markUndone() {
        this.isDone = false;
    }

    /**
     * Generates human‑readable string for console output, includes completion checkbox.
     *
     * @return formatted task string shown to users
     */
    public String toString() {
        String done = isDone() ? "[X] " : "[ ] ";
        return done + this.description;
    }

    /**
     * Produces a formatted text line for saving task into local storage file.
     *
     * @return storage‑friendly string representation
     */
    public String toFileFormat() {
        return (this.isDone ? "1" : "0") + " | " + this.description;
    }

    /**
     * Returns the description of this task.
     *
     * @return task description string
     */
    public String getDescription() {
        return description;
    }
}
