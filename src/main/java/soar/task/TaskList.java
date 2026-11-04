package soar.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Owns the ordered collection of tasks and its list-level operations.
 */
public class TaskList {
    /** Tasks in the same order in which they are shown to the user. */
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing a copy of previously loaded tasks.
     *
     * @param initialTasks Tasks to store in their current order.
     */
    public TaskList(List<Task> initialTasks) {
        Objects.requireNonNull(initialTasks, "Initial tasks must not be null");
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return current task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return selected task
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns a snapshot suitable for display or persistence.
     *
     * <p>The returned list cannot be structurally modified, so callers must use
     * this class's operations to change the task collection.</p>
     *
     * @return immutable snapshot of the tasks in list order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(Objects.requireNonNull(task, "Task must not be null"));
    }

    /**
     * Deletes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @param task Replacement task.
     * @return task that previously occupied the index
     */
    public Task replace(int index, Task task) {
        return tasks.set(index, Objects.requireNonNull(task, "Task must not be null"));
    }

    /**
     * Restores a task at its previous position after a failed save.
     *
     * @param index Zero-based position at which the task belonged.
     * @param task Task to restore.
     */
    public void restoreDeletedTask(int index, Task task) {
        tasks.add(index, Objects.requireNonNull(task, "Task must not be null"));
    }

    /**
     * Marks and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Marked task.
     */
    public Task mark(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Unmarked task.
     */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Restores a task's completion state after a failed save.
     *
     * @param index Zero-based task index.
     * @param wasDone Completion state to restore.
     */
    public void restoreCompletion(int index, boolean wasDone) {
        if (wasDone) {
            mark(index);
        } else {
            unmark(index);
        }
    }

    /**
     * Finds the original zero-based indices of dated tasks occurring on a date.
     *
     * @param date Calendar date to match.
     * @return matching indices in task-list order
     */
    public List<Integer> findIndicesOn(LocalDate date) {
        return IntStream.range(0, tasks.size())
                .filter(index -> isOnDate(tasks.get(index), date))
                .boxed()
                .toList();
    }

    /** Returns whether a deadline or event occurs on the specified date. */
    private boolean isOnDate(Task task, LocalDate date) {
        boolean matchesDate = task instanceof Deadline deadline
                && deadline.getBy().toLocalDate().equals(date);
        return matchesDate || task instanceof Event event && event.occursOn(date);
    }

    /**
     * Finds tasks whose descriptions contain the supplied search text.
     *
     * @param keyword keyword or phrase to find in task descriptions
     * @return matching tasks in their original list order
     */
    public List<Task> findByDescription(String keyword) {
        Objects.requireNonNull(keyword, "Search keyword must not be null");
        return tasks.stream()
                .filter(task -> task.descriptionContains(keyword))
                .toList();
    }
}
