package sevensix;

import java.util.List;

/** Keeps a pre-command state and the affected task together as an immutable undo entry. */
public final class UndoEntry {
    private final TaskSnapshot snapshot;
    private final ChangeType type;
    /** Captures the affected task before the action, independently of subsequent changes. */
    private final Task task;

    /** Captures one change before it modifies the task list.
     *
     * @param tasks the task list before the change.
     * @param type the action about to be performed.
     * @param task the affected task before the action.
     */
    public UndoEntry(Iterable<Task> tasks, ChangeType type, Task task) {
        snapshot = new TaskSnapshot(tasks);
        this.type = type;
        this.task = TaskSnapshot.copyTask(task);
    }

    /** Returns an independent copy of the list before the change.
     *
     * @return the state to restore.
     */
    public List<Task> restoreTasks() {
        return snapshot.restore();
    }

    /** Returns the action that this entry reverses.
     *
     * @return the action type.
     */
    public ChangeType getType() {
        return type;
    }

    /** Returns an independent copy of the affected task before the change.
     *
     * @return the task for the undo confirmation.
     */
    public Task getTask() {
        return TaskSnapshot.copyTask(task);
    }
}
