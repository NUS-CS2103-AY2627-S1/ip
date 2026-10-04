package penny.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import penny.common.PennyException;

/**
 * Represents and manages the collection of tasks in the Penny application.
 * Provides operations to add, delete, mark, unmark, retrieve, and filter tasks,
 * ensuring robust index bounds validation.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with an existing list of tasks.
     *
     * @param tasks The initial list of tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to be added.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task to TaskList";
        int initialSize = this.tasks.size();
        this.tasks.add(task);
        assert this.tasks.size() == initialSize + 1 : "Task list size must increase by 1 after adding task";
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index The 0-based index of the task to remove.
     * @return The removed Task.
     * @throws PennyException If the index is out of bounds or the list is empty.
     */
    public Task delete(int index) throws PennyException {
        validateIndex(index);
        assert index >= 0 && index < this.tasks.size() : "Index must be within bounds after validation";
        int initialSize = this.tasks.size();
        Task removedTask = this.tasks.remove(index);
        assert removedTask != null : "Removed task should not be null";
        assert this.tasks.size() == initialSize - 1 : "Task list size must decrease by 1 after deleting task";
        return removedTask;
    }

    /**
     * Retrieves the task at the specified index.
     *
     * @param index The 0-based index of the task.
     * @return The task at the specified index.
     * @throws PennyException If the index is out of bounds or the list is empty.
     */
    public Task get(int index) throws PennyException {
        validateIndex(index);
        assert index >= 0 && index < this.tasks.size() : "Index must be within bounds after validation";
        Task task = this.tasks.get(index);
        assert task != null : "Retrieved task should not be null";
        return task;
    }

    /**
     * Marks the task at the specified index as completed.
     *
     * @param index The 0-based index of the task to mark.
     * @return The task that was marked done.
     * @throws PennyException If the index is out of bounds or the list is empty.
     */
    public Task mark(int index) throws PennyException {
        validateIndex(index);
        assert index >= 0 && index < this.tasks.size() : "Index must be within bounds after validation";
        Task task = this.tasks.get(index);
        assert task != null : "Target task to mark should not be null";
        task.markAsDone();
        assert task.isDone() : "Task must be marked done after mark()";
        return task;
    }

    /**
     * Marks the task at the specified index as not yet completed.
     *
     * @param index The 0-based index of the task to unmark.
     * @return The task that was unmarked.
     * @throws PennyException If the index is out of bounds or the list is empty.
     */
    public Task unmark(int index) throws PennyException {
        validateIndex(index);
        assert index >= 0 && index < this.tasks.size() : "Index must be within bounds after validation";
        Task task = this.tasks.get(index);
        assert task != null : "Target task to unmark should not be null";
        task.markAsUndone();
        assert !task.isDone() : "Task must be marked undone after unmark()";
        return task;
    }

    /**
     * Validates that the provided index is within the valid bounds of the task list.
     *
     * @param index The 0-based index to validate.
     * @throws PennyException If the task list is empty or the index is out of bounds.
     */
    private void validateIndex(int index) throws PennyException {
        if (this.tasks.isEmpty()) {
            throw new PennyException("Your task list is empty. Add some tasks first!");
        }
        if (index < 0 || index >= this.tasks.size()) {
            throw new PennyException("Task number " + (index + 1) + " is out of range. "
                    + "You currently have " + this.tasks.size() + " task(s).");
        }
    }

    /**
     * Returns the total number of tasks currently in the list.
     *
     * @return The count of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return True if there are no tasks, false otherwise.
     */
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns an unmodifiable list of all tasks.
     *
     * @return List of all tasks.
     */
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    /**
     * Finds and returns all tasks that occur on the specified date.
     *
     * @param targetDate The date to filter tasks by.
     * @return List of tasks occurring on targetDate.
     */
    public List<Task> findTasksOnDate(LocalDate targetDate) {
        assert targetDate != null : "Search targetDate must not be null";
        return this.tasks.stream()
                .filter(task -> task.isOnDate(targetDate))
                .toList();
    }

    /**
     * Finds and returns all tasks whose description matches all keywords in the search query.
     * Supports case-insensitive, word-order independent, and partial-word matching.
     *
     * @param query The space-separated search query.
     * @return List of matching tasks containing all query keywords.
     */
    public List<Task> findTasksByKeyword(String query) {
        assert query != null : "Search query must not be null";
        String trimmed = query.trim();
        if (trimmed.isEmpty()) {
            return Collections.emptyList();
        }
        String[] keywords = trimmed.toLowerCase().split("\\s+");
        return this.tasks.stream()
                .filter(task -> {
                    String description = task.getDescription().toLowerCase();
                    return Arrays.stream(keywords)
                            .allMatch(description::contains);
                })
                .toList();
    }
}
