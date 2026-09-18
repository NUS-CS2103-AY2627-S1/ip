package eden.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Owns and manages Eden's tasks using the one-based task numbers shown to users.
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
     * Creates a task list containing the loaded tasks.
     *
     * @param tasks tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the task with the given one-based task number.
     *
     * @param taskNumber number shown to the user.
     * @return the deleted task.
     * @throws IndexOutOfBoundsException if the task number is outside the list.
     */
    public Task delete(int taskNumber) {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Marks the task with the given one-based task number as completed.
     *
     * @param taskNumber number shown to the user.
     * @return the marked task.
     * @throws IndexOutOfBoundsException if the task number is outside the list.
     */
    public Task mark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Marks the task with the given one-based task number as not completed.
     *
     * @param taskNumber number shown to the user.
     * @return the unmarked task.
     * @throws IndexOutOfBoundsException if the task number is outside the list.
     */
    public Task unmark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.unmark();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a structurally read-only view for displaying or saving the tasks.
     *
     * @return unmodifiable view of the tasks.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Finds tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to find within task descriptions.
     * @return matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        if (keyword.isBlank()) {
            return List.of();
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Gets the task with the given one-based task number.
     */
    private Task getTask(int taskNumber) {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Converts a one-based user-facing task number to a zero-based list index.
     */
    private int toIndex(int taskNumber) {
        return taskNumber - 1;
    }
}
