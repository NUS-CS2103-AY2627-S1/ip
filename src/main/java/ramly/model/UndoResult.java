package ramly.model;

/** Describes a successfully reversed task-list mutation. */
public class UndoResult {
    /** Identifies the kind of mutation that was reversed. */
    public enum Action {
        ADD, DELETE, STATUS_CHANGE
    }

    private final Action action;
    private final Task task;
    private final int taskCount;

    /** Creates a result for a reversed mutation and the affected task. */
    public UndoResult(Action action, Task task, int taskCount) {
        this.action = action;
        this.task = task;
        this.taskCount = taskCount;
    }

    /** Returns the kind of mutation that was reversed. */
    public Action getAction() {
        return action;
    }

    /** Returns the task affected by the reversal. */
    public Task getTask() {
        return task;
    }

    /** Returns the number of tasks after the reversal. */
    public int getTaskCount() {
        return taskCount;
    }
}
