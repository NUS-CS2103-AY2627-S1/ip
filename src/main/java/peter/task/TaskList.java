package peter.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Owns the in-memory collection of tasks and its basic list operations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list holding the given tasks in the order supplied.
     *
     * <p>Being var-args, this one constructor covers both an empty list
     * ({@code new TaskList()}) and a known set of tasks
     * ({@code new TaskList(todo, deadline)}), so a caller that already has the
     * tasks in hand need not wrap them in a collection first.
     *
     * @param tasks initial tasks, in the order they should appear.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Creates a task list containing the supplied tasks in their current order.
     *
     * <p>Kept alongside the var-args constructor for callers such as storage,
     * whose tasks arrive as a collection whose size is unknown until run time.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index task index.
     * @return task at the index.
     */
    public Task get(int index) {
        assert isExistingIndex(index) : "task index out of range: " + index;
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "the task list must not hold a null task";
        tasks.add(task);
    }

    /**
     * Inserts a task at an index, primarily when restoring a failed deletion.
     *
     * @param index position at which to restore the task.
     * @param task task to insert.
     */
    public void add(int index, Task task) {
        assert task != null : "the task list must not hold a null task";
        // Unlike the other index methods this one also accepts the position
        // just past the end, which is where a task deleted from the end of
        // the list is put back.
        assert index >= 0 && index <= tasks.size() : "insertion index out of range: " + index;
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at an index.
     *
     * @param index task index.
     * @return removed task.
     */
    public Task delete(int index) {
        assert isExistingIndex(index) : "task index out of range: " + index;
        return tasks.remove(index);
    }

    /**
     * Changes whether a task is completed.
     *
     * @param index task index.
     * @param isDone new completion status.
     */
    public void setDone(int index, boolean isDone) {
        assert isExistingIndex(index) : "task index out of range: " + index;
        if (isDone) {
            tasks.get(index).markAsDone();
        } else {
            tasks.get(index).unmarkAsDone();
        }
    }

    /**
     * Returns the indices of the tasks satisfying a test, in list order.
     *
     * <p>Indices are returned rather than the tasks themselves so that a caller
     * displaying the results can keep each task's original list number, which
     * is what later mark, unmark, and delete commands refer to.
     *
     * <p>The caller supplies the test as a {@link Predicate}, so one search
     * method serves every kind of search. It is implemented as a stream over
     * the index range: {@code IntStream.range} supplies the indices,
     * {@code filter} keeps those whose task passes the test, and {@code boxed}
     * converts the {@code int} values into the {@code Integer} elements the
     * returned list holds.
     *
     * @param predicate test that a task must pass to be included.
     * @return zero-based indices of the matching tasks.
     */
    public List<Integer> findMatchingIndexes(Predicate<Task> predicate) {
        return IntStream.range(0, tasks.size())
                .filter(index -> predicate.test(tasks.get(index)))
                .boxed()
                .toList();
    }

    /**
     * Provides a read-only view for saving the current tasks.
     *
     * @return unmodifiable task list view.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns whether an index refers to a task currently in the list.
     *
     * <p>Used only by the assertions above, which record that a caller is
     * expected to have validated its task number through
     * {@link peter.Parser#parseTaskIndex} already. An out-of-range index here
     * is therefore a bug in the calling command, not a user mistake.
     *
     * @param index zero-based index to check.
     * @return whether a task exists at the index.
     */
    private boolean isExistingIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
