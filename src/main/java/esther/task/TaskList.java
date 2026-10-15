package esther.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the tasks stored by Esther.
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
     * Creates a task list containing the specified tasks.
     *
     * @param tasks Initial tasks to store.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index Zero-based position of the task.
     * @return Task at the specified index.
     * @throws AssertionError If the index is invalid and assertions are enabled.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task index must refer to an existing task";
        return tasks.get(index);
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     * @throws AssertionError If the task is null and assertions are enabled.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the specified index.
     *
     * @param index Zero-based position of the task.
     * @return Deleted task.
     * @throws AssertionError If the index is invalid and assertions are enabled.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task index must refer to an existing task";
        return tasks.remove(index);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return Number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Counts tasks that have not been completed.
     *
     * @return Number of incomplete tasks.
     */
    public int countNotDone() {
        return (int) tasks.stream()
                .filter(task -> !task.isDone())
                .count();
    }

    /**
     * Stably sorts tasks with incomplete tasks before completed tasks.
     */
    public void sortByCompletionStatus() {
        tasks.sort(Comparator.comparing(Task::isDone));
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .toList();
    }

    /**
     * Returns an unmodifiable copy of the stored tasks.
     *
     * @return Copy of the stored tasks.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
