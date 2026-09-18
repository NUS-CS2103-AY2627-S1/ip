package chris.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import chris.exception.ChrisException;

/**
 * Stores tasks and provides operations for managing them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
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
     * Adds a task to the list.
     *
     * @param task Task to add.
     * @throws ChrisException If a task with the same details already exists.
     */
    public void add(Task task) throws ChrisException {
        boolean isDuplicate = this.tasks.stream()
                .anyMatch(existingTask -> existingTask.hasSameDetails(task));
        if (isDuplicate) {
            throw new ChrisException("That task is already in your list.");
        }
        this.tasks.add(task);
    }

    /**
     * Removes and returns a task from the list.
     *
     * @param index Zero-based index of the task.
     * @return Removed task.
     */
    public Task delete(int index) {
        assertValidIndex(index);
        return this.tasks.remove(index);
    }

    /**
     * Marks a task as completed and returns it.
     *
     * @param index Zero-based index of the task.
     * @return Updated task.
     */
    public Task mark(int index) {
        assertValidIndex(index);
        Task task = this.tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as incomplete and returns it.
     *
     * @param index Zero-based index of the task.
     * @return Updated task.
     */
    public Task unmark(int index) {
        assertValidIndex(index);
        Task task = this.tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index Zero-based index of the task.
     * @return Task at the specified index.
     */
    public Task get(int index) {
        assertValidIndex(index);
        return this.tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of stored tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return this.tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns an unmodifiable view of the stored tasks.
     *
     * @return Unmodifiable task view.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(this.tasks);
    }

    private void assertValidIndex(int index) {
        assert index >= 0 && index < this.tasks.size()
                : "Task index should be within the task list: " + index;
    }
}
