package avon.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores tasks in memory for the duration of an Avon session.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Creates an empty task list.
     */
    public TaskList() {
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks the tasks with which to initialize the list.
     * @throws IllegalArgumentException if the list or one of its tasks is null.
     */
    public TaskList(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("A task list cannot be null.");
        }
        for (Task task : tasks) {
            add(task);
        }
    }

    /**
     * Adds a task of any supported type to the end of the list.
     *
     * @param task the task to store.
     * @throws IllegalArgumentException if the task is null.
     */
    public void add(Task task) {
        if (task == null) {
            // Guard against invalid tasks introduced by future changes.
            throw new IllegalArgumentException("A task cannot be null.");
        }
        tasks.add(task);
    }

    /**
     * Inserts a task at the requested zero-based index.
     *
     * @param index the zero-based position at which to insert the task.
     * @param task the task to store.
     * @throws IllegalArgumentException if the task is null.
     * @throws IndexOutOfBoundsException if the index is outside the insertion range.
     */
    public void add(int index, Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task cannot be null.");
        }
        tasks.add(index, task);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether this list contains no tasks.
     *
     * @return {@code true} if no tasks are stored.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at the requested zero-based index.
     *
     * @param index the zero-based position of the task.
     * @return the task at the requested position.
     * @throws IndexOutOfBoundsException if the index does not refer to a stored task.
     */
    public Task getTask(int index) {
        validateIndex(index);
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the requested zero-based index.
     *
     * @param index the zero-based position of the task to remove.
     * @return the task that was removed.
     * @throws IndexOutOfBoundsException if the index does not refer to a stored task.
     */
    public Task removeTask(int index) {
        validateIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword the text to find in task descriptions.
     * @return matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the one-based number of a task in this list.
     *
     * @param task the stored task whose number is required.
     * @return the task's one-based number.
     * @throws IllegalArgumentException if the task is not in this list.
     */
    public int getTaskNumber(Task task) {
        int taskIndex = tasks.indexOf(task);
        if (taskIndex < 0) {
            throw new IllegalArgumentException("Task is not in the task list.");
        }
        return taskIndex + 1;
    }

    /**
     * Returns whether a stored task has the same details as the given task.
     *
     * @param task the task whose details should be checked.
     * @return {@code true} if an equivalent task is already stored.
     */
    public boolean containsDuplicateOf(Task task) {
        return tasks.stream().anyMatch(storedTask -> storedTask.hasSameDetailsAs(task));
    }

    /**
     * Ensures that an index points to a task currently stored in the list.
     *
     * @param index the zero-based position to validate.
     * @throws IndexOutOfBoundsException if the index does not refer to a stored task.
     */
    private void validateIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index is outside the task list.");
        }
    }
}
