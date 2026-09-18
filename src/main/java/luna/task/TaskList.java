package luna.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Represents the in-memory list of tasks managed by Luna.
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
     * Creates a task list from an existing collection of tasks.
     *
     * @param tasks Existing tasks to copy into this task list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must be created from a non-null collection";
        assert tasks.stream().allMatch(task -> task != null)
                : "A task list must not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list from zero or more tasks.
     *
     * @param tasks Tasks to copy into this task list.
     */
    public TaskList(Task... tasks) {
        assert tasks != null : "A task list must be created from a non-null task array";
        assert Arrays.stream(tasks).allMatch(task -> task != null)
                : "A task list must not contain null tasks";
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task list must not contain null tasks.");
        }
        tasks.add(task);
    }

    /**
     * Returns whether the list already contains a task with the same details.
     *
     * @param candidate Task details to search for.
     * @return {@code true} if an equivalent task is already in the list.
     */
    public boolean containsTaskWithSameDetails(Task candidate) {
        if (candidate == null) {
            return false;
        }

        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index Zero-based position of the task.
     * @return Task at the given index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index Zero-based position of the task.
     * @return Removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Removes every task from the list.
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param index Zero-based position of the task.
     * @return Updated task.
     */
    public Task mark(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param index Zero-based position of the task.
     * @return Updated task.
     */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a copy of the current tasks for read-only style access.
     *
     * @return Copy of the task list contents.
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns the zero-based indices of tasks whose descriptions contain the given keyword.
     *
     * @param keyword Keyword to search for in task descriptions.
     * @return Indices of matching tasks in their original order.
     */
    public List<Integer> findTaskIndices(String keyword) {
        List<Integer> matchingTaskIndices = new ArrayList<>();
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String normalizedDescription = task.getDescription().toLowerCase(Locale.ENGLISH);
            if (normalizedDescription.contains(normalizedKeyword)) {
                matchingTaskIndices.add(i);
            }
        }

        return matchingTaskIndices;
    }
}
