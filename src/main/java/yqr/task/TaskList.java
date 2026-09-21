package yqr.task;

import java.util.ArrayList;
import java.util.List;

import yqr.exception.YqrException;

/**
 * Stores the tasks entered by the user and provides operations on them.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();
    private TaskListState undoState;
    private TaskListState rollbackState;
    private TaskListState rollbackUndoState;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks.addAll(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to store.
     * @throws YqrException if a task with the same type and details already exists.
     */
    public void addTask(Task task) throws YqrException {
        ensureTaskCanBeAdded(task);
        TaskListState stateBeforeChange = captureState();
        tasks.add(task);
        recordChange(stateBeforeChange);
    }

    /**
     * Inserts a task at a one-based position, primarily to restore a failed deletion.
     *
     * @param taskNumber one-based position at which to insert the task.
     * @param task task to insert.
     * @throws YqrException if the position or task is invalid.
     */
    public void insertTask(int taskNumber, Task task) throws YqrException {
        if (taskNumber < 1 || taskNumber > tasks.size() + 1) {
            throw new YqrException("Please input a valid task number");
        }
        ensureTaskCanBeAdded(task);
        TaskListState stateBeforeChange = captureState();
        tasks.add(taskNumber - 1, task);
        recordChange(stateBeforeChange);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return number of stored tasks.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the stored tasks.
     *
     * @return snapshot of the tasks in this list.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to find in task descriptions.
     * @return matching tasks in their original order.
     */
    public List<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Deletes a task from the list.
     *
     * @param taskNumber one-based number of the task to delete.
     * @return the deleted task.
     * @throws YqrException if the task number is outside the list.
     */
    public Task deleteTask(int taskNumber) throws YqrException {
        Task task = getTask(taskNumber);
        TaskListState stateBeforeChange = captureState();
        tasks.remove(taskNumber - 1);
        recordChange(stateBeforeChange);
        return task;
    }

    /**
     * Marks a task as done.
     *
     * @param taskNumber one-based number of the task to mark.
     * @return the task whose status was changed.
     * @throws YqrException if the task number is outside the list.
     */
    public Task markTaskAsDone(int taskNumber) throws YqrException {
        Task task = getTask(taskNumber);
        TaskListState stateBeforeChange = captureState();
        task.markAsDone();
        recordChange(stateBeforeChange);
        return task;
    }

    /**
     * Marks a task as not done.
     *
     * @param taskNumber one-based number of the task to unmark.
     * @return the task whose status was changed.
     * @throws YqrException if the task number is outside the list.
     */
    public Task markTaskAsNotDone(int taskNumber) throws YqrException {
        Task task = getTask(taskNumber);
        TaskListState stateBeforeChange = captureState();
        task.markAsNotDone();
        recordChange(stateBeforeChange);
        return task;
    }

    /**
     * Returns the completion status of a task.
     *
     * @param taskNumber one-based task number.
     * @return whether the task is complete.
     * @throws YqrException if the task number is outside the list.
     */
    public boolean isTaskDone(int taskNumber) throws YqrException {
        return getTask(taskNumber).isDone();
    }

    /** Rejects null or duplicate tasks before they are added. */
    private void ensureTaskCanBeAdded(Task task) throws YqrException {
        if (task == null) {
            throw new YqrException("Task cannot be empty");
        }
        if (tasks.stream().anyMatch(task::hasSameDetails)) {
            throw new YqrException("This task already exists in the list");
        }
    }

    /**
     * Restores the task list to its state before the most recent change.
     *
     * @throws YqrException if no task-changing command can be undone.
     */
    public void undo() throws YqrException {
        if (undoState == null) {
            throw new YqrException("There is no command to undo");
        }
        rollbackState = captureState();
        rollbackUndoState = undoState;
        restoreState(undoState);
        undoState = null;
    }

    /** Discards rollback data after a task change has been saved successfully. */
    public void confirmLastChange() {
        rollbackState = null;
        rollbackUndoState = null;
    }

    /** Restores the state and undo history that existed before the latest task change. */
    public void rollbackLastChange() {
        if (rollbackState == null) {
            throw new IllegalStateException("There is no task change to roll back");
        }
        restoreState(rollbackState);
        undoState = rollbackUndoState;
        confirmLastChange();
    }

    /** Records a completed task change so it can be undone or rolled back. */
    private void recordChange(TaskListState stateBeforeChange) {
        rollbackState = stateBeforeChange;
        rollbackUndoState = undoState;
        undoState = stateBeforeChange;
    }

    /** Creates a snapshot that preserves task order and completion statuses. */
    private TaskListState captureState() {
        return new TaskListState(new ArrayList<>(tasks),
                tasks.stream().map(Task::isDone).toList());
    }

    /** Restores task order and completion statuses from a snapshot. */
    private void restoreState(TaskListState state) {
        tasks.clear();
        tasks.addAll(state.tasks());
        for (int i = 0; i < tasks.size(); i++) {
            if (state.doneStatuses().get(i)) {
                tasks.get(i).markAsDone();
            } else {
                tasks.get(i).markAsNotDone();
            }
        }
    }

    /**
     * Returns the task with the given one-based task number.
     *
     * @param taskNumber one-based task number.
     * @return corresponding task.
     * @throws YqrException if the task number is outside the list.
     */
    private Task getTask(int taskNumber) throws YqrException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new YqrException("Please input a valid task number");
        }
        return tasks.get(taskNumber - 1);
    }

    /** Immutable snapshot used for undo and storage-failure rollback. */
    private record TaskListState(List<Task> tasks, List<Boolean> doneStatuses) {
    }
}
