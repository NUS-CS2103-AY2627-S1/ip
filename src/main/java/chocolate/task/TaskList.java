package chocolate.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import chocolate.exception.ChocolateException;

/**
 * Owns the task collection and provides operations that modify it.
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
     * Creates a task list containing copies of the supplied task references.
     *
     * @param tasks Initial tasks.
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
        int originalSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == originalSize + 1 : "Adding a task must increase the list size by one.";
    }

    /**
     * Returns whether the list already contains a task with the same displayed details.
     *
     * @param task Task to compare with active tasks.
     * @return True when an equivalent task is already active.
     */
    public boolean contains(Task task) {
        String taskDetails = withoutStatus(task);
        return tasks.stream().anyMatch(existingTask -> withoutStatus(existingTask).equals(taskDetails));
    }

    /**
     * Returns a task's display details without its completion marker.
     *
     * @param task Task whose details are needed.
     * @return Task display details that are independent of done status.
     */
    private String withoutStatus(Task task) {
        return task.toString().replace("[X]", "[ ]");
    }

    /**
     * Removes every task from this list.
     */
    public void clear() {
        tasks.clear();
        assert tasks.isEmpty() : "Clearing a task list must leave it empty.";
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param index Zero-based task index.
     * @return Deleted task.
     * @throws ChocolateException If the index does not identify a task.
     */
    public Task delete(int index) throws ChocolateException {
        ensureValidIndex(index);
        int originalSize = tasks.size();
        Task deletedTask = tasks.remove(index);
        assert tasks.size() == originalSize - 1 : "Deleting a task must decrease the list size by one.";
        return deletedTask;
    }

    /**
     * Marks the task at the specified index as complete.
     *
     * @param index Zero-based task index.
     * @throws ChocolateException If the index does not identify a task.
     */
    public void mark(int index) throws ChocolateException {
        ensureValidIndex(index);
        Task task = tasks.get(index);
        task.markAsDone();
        assert task.isDone() : "A marked task must be complete.";
    }

    /**
     * Marks the task at the specified index as incomplete.
     *
     * @param index Zero-based task index.
     * @throws ChocolateException If the index does not identify a task.
     */
    public void unmark(int index) throws ChocolateException {
        ensureValidIndex(index);
        Task task = tasks.get(index);
        task.markAsUndone();
        assert !task.isDone() : "An unmarked task must be incomplete.";
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index Zero-based task index.
     * @return Task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
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
     * Returns a read-only view for services that need to inspect all tasks.
     *
     * @return Unmodifiable view of the task list.
     */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Verifies that an index identifies an existing task.
     *
     * @param index Zero-based task index.
     * @throws ChocolateException If the index does not identify a task.
     */
    private void ensureValidIndex(int index) throws ChocolateException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChocolateException("That task number does not exist in your list!");
        }
    }
}
