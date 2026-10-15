package catgpt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Owns CatGPT's task collection and provides operations that modify it.
 */
public class TaskList implements Iterable<Task> {
    /** Maximum number of tasks that CatGPT can store. */
    public static final int MAX_TASKS = 100;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks Tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        if (tasks.size() > MAX_TASKS) {
            throw new IllegalArgumentException("Too many tasks");
        }
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     * @throws CatGPTException If the task list is full.
     */
    public void add(Task task) throws CatGPTException {
        assert task != null : "Task list cannot contain null tasks";
        if (tasks.size() >= MAX_TASKS) {
            throw new CatGPTException("Your task list is full.");
        }
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return The selected task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return The removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Restores a task at a specific zero-based position.
     *
     * @param index Position at which to insert the task.
     * @param task Task to restore.
     */
    public void restore(int index, Task task) {
        assert index >= 0 && index <= tasks.size() : "Restore index must be within the task list";
        assert task != null : "Restored task must not be null";
        tasks.add(index, task);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ENGLISH)
                        .contains(normalizedKeyword))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Sorts tasks alphabetically by description, ignoring letter case.
     * Tasks with equal descriptions retain their existing relative order.
     */
    public void sortByDescription() {
        tasks.sort(Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Returns an immutable snapshot of the current task order.
     *
     * @return Tasks in their current order.
     */
    public List<Task> snapshot() {
        return List.copyOf(tasks);
    }

    /**
     * Restores the task order from an earlier snapshot.
     *
     * @param previousTasks Tasks in the order to restore.
     */
    public void restoreOrder(List<Task> previousTasks) {
        assert previousTasks != null : "Previous task order must not be null";
        assert previousTasks.size() == tasks.size() : "Restored task order must contain every current task";
        tasks.clear();
        tasks.addAll(previousTasks);
    }

    /**
     * Returns an iterator over the tasks in list order.
     *
     * @return Task iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
