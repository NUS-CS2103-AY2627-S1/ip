package auto.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import auto.exception.AutoException;

/** Owns the task collection and its user-facing, one-based operations. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing a defensive copy of the supplied tasks. */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task identified by its one-based number. */
    public Task get(int taskNumber) throws AutoException {
        return tasks.get(toIndex(taskNumber));
    }

    /** Removes and returns the task identified by its one-based number. */
    public Task delete(int taskNumber) throws AutoException {
        return tasks.remove(toIndex(taskNumber));
    }

    /** Marks and returns the task identified by its one-based number. */
    public Task mark(int taskNumber) throws AutoException {
        Task task = get(taskNumber);
        task.mark();
        return task;
    }

    /** Unmarks and returns the task identified by its one-based number. */
    public Task unmark(int taskNumber) throws AutoException {
        Task task = get(taskNumber);
        task.unmark();
        return task;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns a read-only view for display and persistence. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /** Restores a deletion that could not be persisted. */
    public void restoreDeleted(int taskNumber, Task task) {
        // A failed deletion must be undone at its original position, including the former last slot.
        assert taskNumber >= 1 && taskNumber <= tasks.size() + 1
                : "Deletion rollback requires a valid original position";
        tasks.add(taskNumber - 1, task);
    }

    /** Reverses the most recent addition when it could not be persisted. */
    public void removeLast() {
        // Addition rollback is only called after a task has been appended successfully.
        assert !tasks.isEmpty() : "Addition rollback requires an appended task";
        tasks.remove(tasks.size() - 1);
    }

    /**
     * Validates a user-facing task number and converts it to a list index.
     *
     * @param taskNumber one-based task number
     * @return corresponding zero-based list index
     * @throws AutoException if the task number is outside the list
     */
    private int toIndex(int taskNumber) throws AutoException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw AutoException.noSuchTask(taskNumber);
        }
        return taskNumber - 1;
    }
}
