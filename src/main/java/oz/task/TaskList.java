package oz.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import oz.exception.OzException;

/**
 * Represents the list of tasks and provides operations to manipulate tasks.
 */
public class TaskList {
    /** Error shown when a task index does not identify an existing task. */
    private static final String TASK_NOT_FOUND_MESSAGE = "That task number does not exist.";

    /** Internal list storing task objects. */
    private final List<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with existing tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks == null || tasks.stream().noneMatch(task -> task == null)
                : "A task list must not contain null tasks";
        this.tasks = tasks == null ? new ArrayList<>() : new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task";
        this.tasks.add(task);
    }

    /**
     * Inserts a task at the specified zero-based index.
     *
     * @param index Zero-based index at which the task is inserted.
     * @param task The task to insert.
     */
    public void add(int index, Task task) {
        assert task != null : "Cannot add a null task";
        assert index >= 0 && index <= this.tasks.size() : "Insert index out of bounds";
        this.tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The removed task.
     * @throws OzException If the index is out of bounds.
     */
    public Task delete(int index) throws OzException {
        checkIndex(index);
        return this.tasks.remove(index);
    }

    /**
     * Retrieves the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task.
     * @return The task at the specified index.
     * @throws OzException If the index is out of bounds.
     */
    public Task get(int index) throws OzException {
        checkIndex(index);
        return this.tasks.get(index);
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return Size of the task list.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns an immutable snapshot of tasks in their existing order.
     *
     * @return Immutable task snapshot.
     */
    public List<Task> getTasks() {
        return List.copyOf(this.tasks);
    }

    /**
     * Finds and returns all tasks that occur on the specified date.
     *
     * @param date The date to filter tasks by.
     * @return List of matching tasks occurring on that date.
     */
    public List<Task> findTasksOn(LocalDate date) {
        assert date != null : "The target date must be non-null";
        return this.tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Finds and returns all tasks whose descriptions contain the specified keyword.
     *
     * @param keyword The keyword to filter tasks by.
     * @return List of tasks matching the keyword.
     */
    public List<Task> findTasksByKeyword(String keyword) {
        return this.tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .toList();
    }

    /**
     * Marks the task at the specified zero-based index as completed.
     *
     * @param index Zero-based index of the task.
     * @throws OzException If the index is out of bounds.
     */
    public void markAsDone(int index) throws OzException {
        checkIndex(index);
        this.tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the specified zero-based index as not completed.
     *
     * @param index Zero-based index of the task.
     * @throws OzException If the index is out of bounds.
     */
    public void markAsNotDone(int index) throws OzException {
        checkIndex(index);
        this.tasks.get(index).markAsNotDone();
    }

    /**
     * Validates that the specified zero-based index is within list bounds.
     *
     * @param index Index to check.
     * @throws OzException If the index is negative or out of range.
     */
    private void checkIndex(int index) throws OzException {
        if (index < 0 || index >= this.tasks.size()) {
            throw new OzException(TASK_NOT_FOUND_MESSAGE);
        }
    }
}
