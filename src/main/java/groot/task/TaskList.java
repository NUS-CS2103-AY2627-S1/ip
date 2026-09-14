package groot.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Owns the task collection and provides operations that may change it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a defensive copy of the supplied tasks.
     *
     * @param tasks Initial tasks, in display order.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list from tasks supplied directly by the caller.
     *
     * @param tasks Initial tasks, in display order.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a specific position, such as when rolling back a deletion.
     *
     * @param index Zero-based insertion position.
     * @param task Task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the given position.
     *
     * @param index Zero-based task position.
     * @return Removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index Zero-based task position.
     * @return Selected task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Marks and returns the task at the given position.
     *
     * @param index Zero-based task position.
     * @return Updated task.
     */
    public Task markAsDone(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        // Every task subtype must honor the completion operation before success is reported.
        assert task.isDone() : "Marking a task must leave it completed";
        return task;
    }

    /**
     * Unmarks and returns the task at the given position.
     *
     * @param index Zero-based task position.
     * @return Updated task.
     */
    public Task markAsNotDone(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        // Every task subtype must honor the inverse operation before success is reported.
        assert !task.isDone() : "Unmarking a task must leave it incomplete";
        return task;
    }

    /**
     * Sets a task's status explicitly, primarily to roll back a failed save.
     *
     * @param index Zero-based task position.
     * @param isDone Status to restore.
     */
    public void setDone(int index, boolean isDone) {
        if (isDone) {
            tasks.get(index).markAsDone();
        } else {
            tasks.get(index).markAsNotDone();
        }
        // Failed saves rely on this operation to restore the exact previous completion state.
        assert tasks.get(index).isDone() == isDone : "Restored status must match the requested status";
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable snapshot suitable for saving to disk.
     *
     * @return Snapshot of the tasks in display order.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns deadline positions ordered by date, then by case-insensitive description.
     * Equal dates and descriptions retain their main-list order. Neither tasks nor the main list change.
     *
     * @param isReversed Whether to reverse date order only, leaving description ties in alphabetical order.
     * @return Unmodifiable zero-based main-list indices, valid until tasks are added or removed.
     */
    public List<Integer> getSortedDeadlineIndices(boolean isReversed) {
        Comparator<Integer> dateOrder = Comparator.comparing(index -> ((Deadline) tasks.get(index)).getDueDate());
        if (isReversed) {
            dateOrder = dateOrder.reversed();
        }
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index) instanceof Deadline)
                .boxed()
                .sorted(dateOrder.thenComparing(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT)))
                .toList();
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword Keyword or phrase to search for.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.matches(keyword))
                .toList();
    }
}
