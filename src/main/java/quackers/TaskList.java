package quackers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Owns the task collection and the operations that modify it.
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
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns a read-only snapshot of the tasks.
     *
     * @return the current tasks
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Finds tasks whose descriptions contain a keyword, ignoring letter case.
     *
     * @param keyword the text to search for.
     * @return the matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param taskIndex the zero-based task index.
     * @return the removed task
     * @throws QuackersException if the index is outside the list
     */
    public Task delete(int taskIndex) throws QuackersException {
        validateIndex(taskIndex);
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task index must refer to an existing task.";
        return tasks.remove(taskIndex);
    }

    /**
     * Changes and returns the completion status of a numbered task.
     *
     * @param taskIndex the zero-based task index.
     * @param isDone whether the task should be completed.
     * @return the updated task
     * @throws QuackersException if the index is outside the list
     */
    public Task updateStatus(int taskIndex, boolean isDone) throws QuackersException {
        validateIndex(taskIndex);
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task index must refer to an existing task.";
        Task task = tasks.get(taskIndex);
        if (!task.supportsCompletionStatus()) {
            throw new QuackersException("Quack? Notes do not have a completion status.");
        }
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        return task;
    }

    private void validateIndex(int taskIndex) throws QuackersException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new QuackersException("Quack? Please enter a task number from the list.");
        }
    }
}
