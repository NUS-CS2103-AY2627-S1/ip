package nyonbot.model;

/**
 * Represents a task that can be completed or left incomplete.
 *
 * @author nje14
 */
public class Task {
    protected String taskName;
    protected boolean isDone = false;

    /**
     * Creates an incomplete task with the specified name.
     *
     * @param taskName task name.
     * @throws IllegalArgumentException if the name cannot be represented in storage.
     */
    public Task(String taskName) {
        validateTaskName(taskName);
        this.taskName = taskName;
    }

    private void validateTaskName(String taskName) {
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("task description cannot be blank");
        }
        if (taskName.indexOf('|') >= 0 || taskName.indexOf('\n') >= 0
                || taskName.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(
                    "task description cannot contain | or line breaks");
        }
    }

    /**
     * Marks task as completed.
     */
    public void completeTask() {
        this.isDone = true;
    }

    /**
     * Marks task as incomplete.
     */
    public void uncompleteTask() {
        this.isDone = false;
    }

    /**
     * Returns whether the tasks have the same name.
     *
     * @param task task name to compare.
     * @return whether task names are equal.
     */
    public boolean isSameTask(String task) {
        return this.taskName.equals(task);
    }

    public boolean isDone() {
        return this.isDone;
    }

    public String getName() {
        return this.taskName;
    }

    @Override
    public String toString() {
        return String.format("[T][%s] %s", isDone ? "X" : " ", this.taskName);
    }
}
