package tony.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owns the application's tasks and provides operations for changing the list.
 */
public class TaskList {
    /** The tasks currently managed by the application. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     * The tasks are copied so callers cannot later change this list indirectly.
     * Accepting a list instead of varargs also lets callers pass an existing task collection directly.
     *
     * @param tasks tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "The source task collection must not be null";
        assert tasks.stream().noneMatch(Objects::isNull) : "A task list must not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     * Accepting one task instead of varargs keeps each call aligned with one user action.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Returns whether the list already contains a task with the same type and details.
     *
     * @param task task whose details should be checked.
     * @return whether a matching task is already present.
     */
    public boolean containsSameDetails(Task task) {
        assert task != null : "A task to compare must not be null";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param taskIndex zero-based index of the task to remove.
     * @return the removed task.
     */
    public Task delete(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Removes and returns the tasks at the supplied zero-based indexes.
     * Returned tasks follow their original list order, regardless of the order of the indexes.
     *
     * @param taskIndexes distinct zero-based indexes of tasks to remove.
     * @return the removed tasks in their original list order.
     */
    public List<Task> deleteTasks(List<Integer> taskIndexes) {
        assert taskIndexes != null : "Task indexes to delete must not be null";
        assert taskIndexes.stream().noneMatch(Objects::isNull) : "Task indexes to delete must not contain null";
        assert taskIndexes.stream().distinct().count() == taskIndexes.size()
                : "Task indexes to delete must be distinct";
        assert taskIndexes.stream().allMatch(index -> index >= 0 && index < tasks.size())
                : "Task indexes to delete must refer to existing tasks";

        ArrayList<Integer> sortedTaskIndexes = new ArrayList<>(taskIndexes);
        sortedTaskIndexes.sort(Integer::compareTo);

        ArrayList<Task> deletedTasks = new ArrayList<>();
        for (int taskIndex : sortedTaskIndexes) {
            deletedTasks.add(tasks.get(taskIndex));
        }
        for (int index = sortedTaskIndexes.size() - 1; index >= 0; index--) {
            tasks.remove((int) sortedTaskIndexes.get(index));
        }
        return List.copyOf(deletedTasks);
    }

    /**
     * Marks the task at a zero-based index as complete.
     *
     * @param taskIndex zero-based index of the task to mark.
     * @return the task after it has been marked.
     */
    public Task mark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at a zero-based index as incomplete.
     *
     * @param taskIndex zero-based index of the task to unmark.
     * @return the task after it has been unmarked.
     */
    public Task unmark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsUndone();
        return task;
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword, ignoring letter case.
     * The returned list is independent of this list, but contains the same task objects.
     *
     * @param keyword text to search for in task descriptions.
     * @return matching tasks in their original list order.
     */
    public TaskList find(String keyword) {
        assert keyword != null : "A search keyword must not be null";
        assert !keyword.isBlank() : "A search keyword must not be blank";

        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.descriptionContains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return new TaskList(matchingTasks);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param taskIndex zero-based index of the task.
     * @return the requested task.
     */
    public Task get(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns how many tasks have been completed.
     *
     * @return the number of completed tasks.
     */
    public int countCompletedTasks() {
        int completedTaskCount = 0;
        for (Task task : tasks) {
            if (task.isDone()) {
                completedTaskCount++;
            }
        }
        return completedTaskCount;
    }

    /**
     * Returns an immutable snapshot for saving the current tasks.
     *
     * @return a snapshot of the tasks in list order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
