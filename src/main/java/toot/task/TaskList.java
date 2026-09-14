package toot.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Owns the collection of tasks and provides task-list operations.
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
     * @param initialTasks Tasks with which to initialize the list.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Only actual tasks may be added to the list.";
        tasks.add(task);
    }

    /**
     * Marks the task at a zero-based index as completed.
     *
     * @param index Zero-based index of the task.
     * @return Updated task.
     */
    public Task mark(int index) {
        assert index >= 0 && index < tasks.size() : "The parser must validate task indexes.";
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at a zero-based index as not completed.
     *
     * @param index Zero-based index of the task.
     * @return Updated task.
     */
    public Task unmark(int index) {
        assert index >= 0 && index < tasks.size() : "The parser must validate task indexes.";
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes the task at a zero-based index.
     *
     * @param index Zero-based index of the task.
     * @return Removed task.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "The parser must validate task indexes.";
        return tasks.remove(index);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns an unmodifiable view of the tasks.
     *
     * @return Current tasks in list order.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }
}
