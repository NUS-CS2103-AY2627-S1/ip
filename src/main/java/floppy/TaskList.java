package floppy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Owns the task collection and provides validated task-list operations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks to copy into the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
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
     * Removes and returns the task at the specified index.
     *
     * @param taskIndex Zero-based index of the task to remove.
     * @return Removed task.
     * @throws FloppyException If the index is outside the task list.
     */
    public Task delete(int taskIndex) throws FloppyException {
        Task task = get(taskIndex);
        tasks.remove(taskIndex);
        return task;
    }

    /**
     * Marks and returns the selected task as completed.
     *
     * @param taskIndex Zero-based index of the task to mark.
     * @return Updated task.
     * @throws FloppyException If the index is outside the task list.
     */
    public Task mark(int taskIndex) throws FloppyException {
        Task task = get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks and returns the selected task as not completed.
     *
     * @param taskIndex Zero-based index of the task to unmark.
     * @return Updated task.
     * @throws FloppyException If the index is outside the task list.
     */
    public Task unmark(int taskIndex) throws FloppyException {
        Task task = get(taskIndex);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns an immutable snapshot of the stored tasks.
     *
     * @return Current tasks in list order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ENGLISH)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return Number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index after validating the index.
     *
     * @param taskIndex Zero-based index of the task to retrieve.
     * @return Selected task.
     * @throws FloppyException If the index is outside the task list.
     */
    private Task get(int taskIndex) throws FloppyException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new FloppyException("Choose a task number shown in the list.");
        }
        return tasks.get(taskIndex);
    }
}
