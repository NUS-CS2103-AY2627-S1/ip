package larper.task;

/**
 * Represents a task found by search together with its original task number.
 */
public class FindResult {
    private int taskNumber;
    private Task task;

    /**
     * Creates a search result for a task at the specified task number.
     *
     * @param taskNumber Original one-based task number in the task list.
     * @param task Task that matched the search phrase.
     */
    public FindResult(int taskNumber, Task task) {
        assert taskNumber >= 1 : "Find results should keep a one-based task number.";
        assert task != null : "Find results should always reference a matched task.";
        this.taskNumber = taskNumber;
        this.task = task;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public Task getTask() {
        return task;
    }

    /**
     * Returns the task with its original task number shown beside it.
     */
    @Override
    public String toString() {
        return task + " (task no: " + taskNumber + ")";
    }
}
