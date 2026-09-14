package penguin.task;

/**
 * Associates a matching task with its one-based position in the complete task list.
 */
public final class TaskSearchResult {
    /** One-based task number from the complete task list. */
    private final int taskNumber;

    /** Independent copy of the matching task. */
    private final Task task;

    /**
     * Creates a result for a task at its original list position.
     *
     * @param originalTaskNumber one-based task number from the complete list.
     * @param matchingTask matching task.
     */
    TaskSearchResult(int originalTaskNumber, Task matchingTask) {
        this.taskNumber = originalTaskNumber;
        this.task = matchingTask.copy();
    }

    /**
     * Returns the task's one-based number from the complete list.
     *
     * @return original one-based task number.
     */
    public int getTaskNumber() {
        return taskNumber;
    }

    /**
     * Returns an independent copy of the matching task.
     *
     * @return matching task copy.
     */
    public Task getTask() {
        return task.copy();
    }
}
