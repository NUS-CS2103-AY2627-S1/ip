package bubba;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Owns an ordered task list and provides operations using one-based task numbers.
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
     * Creates a list containing the supplied task references in their existing order.
     * Later additions or removals in the supplied list do not affect this list.
     *
     * @param tasks Initial tasks to copy into the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the specified one-based position.
     *
     * @param taskNumber Task number shown to the user.
     * @return Task at that position.
     * @throws BubbaException If the number is outside the list's bounds.
     */
    public Task get(int taskNumber) throws BubbaException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BubbaException("Task does not exist.");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes a task, preserving the order of the remaining tasks.
     *
     * @param taskNumber One-based number of the task to remove.
     * @return Removed task.
     * @throws BubbaException If the number is outside the list's bounds; the list is unchanged.
     */
    public Task delete(int taskNumber) throws BubbaException {
        Task task = get(taskNumber);
        tasks.remove(taskNumber - 1);
        return task;
    }

    /**
     * Updates a task's completion status.
     *
     * @param taskNumber One-based number of the task to update.
     * @param isDone Whether the task should be marked as completed.
     * @return Updated task.
     * @throws BubbaException If the number is outside the list's bounds.
     */
    public Task mark(int taskNumber, boolean isDone) throws BubbaException {
        Task task = get(taskNumber);
        if (isDone) {
            task.done();
        } else {
            task.undoDone();
        }
        return task;
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
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     * The matches retain their order from the full task list.
     *
     * @param keyword Text to find within task descriptions.
     * @return Matching tasks in their original order.
     */
    public ArrayList<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.description.toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a shallow copy of the list in its current order.
     * Adding or removing entries in the copy does not affect this list, but the task objects are shared.
     *
     * @return Copy containing the same task references.
     */
    public ArrayList<Task> toList() {
        return new ArrayList<>(tasks);
    }
}
