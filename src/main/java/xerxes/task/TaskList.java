package xerxes.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Manages an ordered collection of tasks.
 */
public class TaskList implements Iterable<Task> {
    /** Tasks in the order they were added or loaded. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks Tasks to add to this list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must be created from a non-null collection";
        this.tasks = new ArrayList<>(tasks);
        assert this.tasks.stream().allMatch(task -> task != null)
                : "A task list cannot contain null tasks";

    }

    /**
     * Changes the completion status of the task at the given zero-based index.
     *
     * @param taskIndex Zero-based index of the task to update.
     * @param isCompleted New completion status.
     * @return The updated task.
     * @throws IllegalArgumentException If {@code taskIndex} is outside this list.
     */
    public Task handleCompletionStatus(int taskIndex, boolean isCompleted) {
        validateIndex(taskIndex);
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "validateIndex must accept a valid task index";

        Task task = tasks.get(taskIndex);

        if (isCompleted) {
            task.markCompleted();
        } else {
            task.markUncompleted();
        }
        return task;
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param taskIndex Zero-based index of the task to remove.
     * @return The removed task.
     * @throws IllegalArgumentException If {@code taskIndex} is outside this list.
     */
    public Task deleteTask(int taskIndex) {
        validateIndex(taskIndex);
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "validateIndex must accept a valid task index";
        Task task = tasks.get(taskIndex);
        tasks.remove(taskIndex);
        return task;
    }

    /**
     * Validates that an index identifies a task in this list.
     *
     * @param index Zero-based index to validate.
     * @throws IllegalArgumentException If the index is outside this list.
     */
    public void validateIndex(int index) {
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("Wait ah, there are no tasks in your list lah.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new IllegalArgumentException(
                    "Yo your task number cannot lah! Give me an index between 1 and " + tasks.size() + ".");
        }
    }

    /**
     * Appends a task to this list if an equivalent task is not already present.
     *
     * @param task Task to add.
     * @throws IllegalArgumentException If an equivalent task already exists.
     */
    public void addTask(Task task) {
        assert task != null : "A task list cannot contain a null task";
        if (tasks.contains(task)) {
            throw new IllegalArgumentException("Wah, that task already exists ah.");
        }
        this.tasks.add(task);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword, ignoring case.
     *
     * @param keyword keyword to search for.
     * @return matching tasks in their original list order.
     */
    public List<Task> findMatchingTasks(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getTaskName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns all tasks as a numbered, line-separated list.
     *
     * @return Formatted representation of this task list.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int i = 1;
        for (Task task : tasks) {
            if (i > 1) {
                result.append("\n");
            }
            result.append(i).append(": ").append(task);
            i++;
        }
        return result.toString();
    }

    /**
     * Returns an iterator over the tasks in list order.
     *
     * @return Iterator over the contained tasks.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
