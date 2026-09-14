package penguin.task;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores the tasks entered by the user.
 */
public class TaskList {
    /** Maximum number of task-list states retained for undo operations. */
    private static final int MAX_UNDO_STEPS = 5;

    /** Tasks in their current display order. */
    private final List<Task> tasks;

    /** Previous task-list states, with the most recent state first. */
    private final Deque<List<Task>> history = new ArrayDeque<>();

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the end of the task list.
     *
     * @param task the task to add.
     */
    public void addTask(Task task) {
        assert task != null : "Task must not be null";

        saveState();
        tasks.add(task);
    }

    /**
     * Adds a task loaded from storage without creating an undo history entry.
     *
     * @param task task restored from persistent storage.
     */
    public void addLoadedTask(Task task) {
        assert task != null : "Loaded task must not be null";

        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the specified index.
     *
     * @param index zero-based index of the task to delete.
     * @return the deleted task.
     * @throws IndexOutOfBoundsException if the index is invalid.
     */
    public Task deleteTask(int index) {
        Task task = tasks.get(index);
        saveState();
        tasks.remove(index);
        return task;
    }

    /**
     * Marks the task at the specified index as completed.
     *
     * @param index zero-based index of the task to mark.
     * @return the task after it has been marked as completed.
     * @throws IndexOutOfBoundsException if the index is invalid.
     * @throws IllegalStateException if the task is already completed.
     */
    public Task markTask(int index) {
        Task task = tasks.get(index);
        if (task.isDone()) {
            throw new IllegalStateException("This task is already marked.");
        }
        saveState();
        task.markDone();
        return task;
    }

    /**
     * Marks the task at the specified index as incomplete.
     *
     * @param index zero-based index of the task to unmark.
     * @return the task after it has been marked as incomplete.
     * @throws IndexOutOfBoundsException if the index is invalid.
     * @throws IllegalStateException if the task is already incomplete.
     */
    public Task unmarkTask(int index) {
        Task task = tasks.get(index);
        if (!task.isDone()) {
            throw new IllegalStateException("This task is already unmarked.");
        }
        saveState();
        task.markUndone();
        return task;
    }

    /**
     * Returns a copy of the current tasks for display purposes.
     *
     * @return copy of the current task list.
     */
    public List<Task> getTasks() {
        return copyTasks();
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether the task list is empty.
     *
     * @return true if the task list is empty, otherwise false.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Restores the most recent task-list state.
     *
     * @throws IllegalStateException if there is no previous task-list state.
     */
    public void undo() {
        if (history.isEmpty()) {
            throw new IllegalStateException("There is nothing to undo.");
        }
        tasks.clear();
        tasks.addAll(history.pop());
    }

    /**
     * Captures the current tasks and undo history for transactional recovery.
     *
     * @return independent snapshot of the current state.
     */
    public TaskListSnapshot createSnapshot() {
        return new TaskListSnapshot(tasks, history);
    }

    /**
     * Restores the tasks and undo history stored in a snapshot.
     *
     * @param snapshot state to restore.
     */
    public void restoreSnapshot(TaskListSnapshot snapshot) {
        assert snapshot != null : "Task-list snapshot must not be null";

        tasks.clear();
        tasks.addAll(snapshot.copyTasks());
        history.clear();
        history.addAll(snapshot.copyHistory());
    }

    /**
     * Saves an independent state snapshot and keeps only MAX_UNDO_STEPS snapshots.
     */
    private void saveState() {
        history.push(copyTasks());
        if (history.size() > MAX_UNDO_STEPS) {
            history.removeLast();
        }
    }

    /**
     * Creates independent copies of all tasks in their current order.
     *
     * @return independent task copies in their current order.
     */
    private List<Task> copyTasks() {
        return tasks.stream()
                .map(Task::copy)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all tasks converted into lines for persistent storage.
     *
     * @return task data formatted as storage lines.
     */
    public List<String> toStorageLines() {
        return tasks.stream()
                .map(Task::toStorageFormat)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds deadlines and events occurring on a specified date.
     *
     * @param date date to search for.
     * @return matching tasks with their original one-based task numbers.
     */
    public List<TaskSearchResult> findTasksOnDate(LocalDate date) {
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).occursOn(date))
                .mapToObj(index -> new TaskSearchResult(index + 1, tasks.get(index)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword keyword or phrase to search for.
     * @return matching tasks in their original order and with their original
     *         one-based task numbers.
     */
    public List<TaskSearchResult> findMatchingTasks(String keyword) {
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).containsKeyword(keyword))
                .mapToObj(index -> new TaskSearchResult(index + 1, tasks.get(index)))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
