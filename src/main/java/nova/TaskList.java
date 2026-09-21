package nova;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages Nova's collection of tasks.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the specified tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the specified task.
     *
     * @param taskNumber One-based task number.
     * @return Deleted task.
     */
    public Task delete(int taskNumber) {
        int index = getIndex(taskNumber);
        return tasks.remove(index);
    }

    /**
     * Marks the specified task as done.
     *
     * @param taskNumber One-based task number.
     */
    public void markDone(int taskNumber) {
        int index = getIndex(taskNumber);
        tasks.get(index).markDone();
    }

    /**
     * Returns the specified task.
     *
     * @param taskNumber One-based task number.
     * @return Requested task.
     */
    public Task get(int taskNumber) {
        int index = getIndex(taskNumber);
        return tasks.get(index);
    }

    /**
     * Updates the specified task without changing its position in the list.
     *
     * @param taskNumber One-based task number.
     * @param description Replacement description.
     * @return Updated task.
     * @throws IllegalArgumentException If the task number or description is invalid.
     */
    public Task update(int taskNumber, String description) {
        Task task = get(taskNumber);
        task.updateDescription(description);
        return task;
    }

    /**
     * Returns all tasks as an unmodifiable list.
     *
     * @return Unmodifiable task list.
     */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns an independent copy of this task list.
     *
     * @return Copy containing the same task data.
     */
    public TaskList copy() {
        List<Task> copiedTasks = tasks.stream()
                .map(task -> Task.fromStorageString(task.toStorageString()))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(copiedTasks);
    }

    /**
     * Replaces all tasks with those in the specified list.
     *
     * @param replacement Replacement task list.
     */
    public void replaceWith(TaskList replacement) {
        tasks.clear();
        tasks.addAll(replacement.tasks);
    }

    /**
     * Finds tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int getIndex(int taskNumber) {
        int index = taskNumber - 1;

        if (index < 0 || index >= tasks.size()) {
            throw new IllegalArgumentException(
                    "That task number does not exist.");
        }

        return index;
    }
}
