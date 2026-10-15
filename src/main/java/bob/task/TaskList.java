package bob.task;

import java.util.ArrayList;
import java.util.List;

import bob.BobException;

/**
 * Owns Bob's task collection and provides operations for changing it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in the same order.
     *
     * @param tasks initial tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index index of the task
     * @return task at the given index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return current task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Validates a one-based task number and converts it to a zero-based index.
     *
     * @param taskNumber one-based number entered by the user
     * @return corresponding zero-based list index
     * @throws BobException if the list is empty or the task number is out of range
     */
    public int getTaskIndex(int taskNumber) throws BobException {
        if (tasks.isEmpty()) {
            throw new BobException("can't do anyth if there's no task");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BobException("task doesn't exist, whats your fav no from 1 to " + tasks.size() + "?");
        }
        int index = taskNumber - 1;
        // Successful validation must produce an index that list operations can safely use.
        assert index >= 0 && index < tasks.size() : "Validated task number must map to an existing task";
        return index;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Checks whether a task with the same type, description, dates, and priority exists.
     * Completion state does not distinguish task details.
     *
     * @param candidate task to check
     * @return whether the task is already present
     */
    public boolean containsDetails(Task candidate) {
        String candidateDetails = candidate.toStorageString().replaceFirst("^([TDE] \\| )[01]", "$10");
        return tasks.stream().anyMatch(task -> task.toStorageString()
                .replaceFirst("^([TDE] \\| )[01]", "$10").equals(candidateDetails));
    }

    /**
     * Deletes and returns the task at a zero-based index.
     *
     * @param index index of the task to delete
     * @return deleted task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks a task as completed.
     *
     * @param index index of the task to mark
     */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks a task as incomplete.
     *
     * @param index index of the task to unmark
     */
    public void unmark(int index) {
        tasks.get(index).markAsNotDone();
    }

    /**
     * Returns a read-only snapshot for displaying or saving the tasks.
     *
     * @return unmodifiable snapshot of the current tasks
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to find
     * @return matching tasks in their original order
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .toList();
    }

    /**
     * Restores a deleted task to its original position after a failed save.
     *
     * @param index original index of the task
     * @param task task to restore
     */
    public void restoreDeletedTask(int index, Task task) {
        tasks.add(index, task);
        // Rollback must restore the same object at its original position, preserving order and state.
        assert tasks.get(index) == task : "Deleted task must be restored at its original index";
    }

    /**
     * Restores a task's previous completion state after a failed save.
     *
     * @param index index of the task to restore
     * @param wasDone completion state before the attempted change
     */
    public void restoreDoneState(int index, boolean wasDone) {
        if (wasDone) {
            mark(index);
        } else {
            unmark(index);
        }
        // A failed save must leave the completion state exactly as it was before the command.
        assert tasks.get(index).isDone() == wasDone : "Rollback must restore the previous completion state";
    }
}
