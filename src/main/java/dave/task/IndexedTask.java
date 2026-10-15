package dave.task;

/**
 * Associates a {@link Task} with its 1-based index in the task list.
 */
public class IndexedTask {

    /** 1-based index of the task in the task list. */
    private final int originalIndex;
    /** The task instance. */
    private final Task task;

    /**
     * Constructs a new IndexedTask with the given 1-based index and task.
     *
     * @param originalIndex 1-based position of the task in the task list.
     * @param task Task instance.
     */
    public IndexedTask(int originalIndex, Task task) {
        assert originalIndex > 0 : "Original task index must be a positive 1-based integer";
        assert task != null : "Task cannot be null";
        this.originalIndex = originalIndex;
        this.task = task;
    }

    /**
     * Returns the 1-based index of the task in the original list.
     *
     * @return 1-based task index.
     */
    public int getOriginalIndex() {
        return this.originalIndex;
    }

    /**
     * Returns the task instance.
     *
     * @return Task instance.
     */
    public Task getTask() {
        return this.task;
    }
}
