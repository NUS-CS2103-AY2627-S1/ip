package bobo.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bobo.exception.BoboException;

/**
 * Manages the list of tasks for the Bobo application.
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
     * Constructs a TaskList initialized with a list of tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = (tasks != null) ? new ArrayList<>(tasks) : new ArrayList<>();
        assert this.tasks != null : "Internal tasks list must be initialized";
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to be added.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task to TaskList";
        tasks.add(task);
    }

    /**
     * Deletes the task at the specified 1-based index.
     *
     * @param index 1-based task index.
     * @return The removed task.
     * @throws BoboException If index is out of bounds.
     */
    public Task delete(int index) throws BoboException {
        validateIndex(index);
        assert index > 0 && index <= tasks.size() : "Task index must be within bounds after validation";
        return tasks.remove(index - 1);
    }

    /**
     * Marks the task at the specified 1-based index as done.
     *
     * @param index 1-based task index.
     * @return The marked task.
     * @throws BoboException If index is out of bounds.
     */
    public Task mark(int index) throws BoboException {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the specified 1-based index as not done yet.
     *
     * @param index 1-based task index.
     * @return The unmarked task.
     * @throws BoboException If index is out of bounds.
     */
    public Task unmark(int index) throws BoboException {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Retrieves the task at the specified 1-based index.
     *
     * @param index 1-based task index.
     * @return The task at index.
     * @throws BoboException If index is out of bounds.
     */
    public Task get(int index) throws BoboException {
        validateIndex(index);
        assert index > 0 && index <= tasks.size() : "Task index must be within bounds after validation";
        return tasks.get(index - 1);
    }

    /**
     * Returns a list of tasks occurring on the target date.
     *
     * @param targetDate The date to filter tasks by.
     * @return List of tasks occurring on targetDate.
     */
    public List<Task> getTasksOnDate(LocalDate targetDate) {
        if (targetDate == null) {
            return new ArrayList<>();
        }
        return tasks.stream()
                .filter(task -> task.isOnDate(targetDate))
                .collect(Collectors.toList());
    }

    /**
     * Finds and returns a list of tasks whose description contains the keyword.
     *
     * @param keyword Keyword to search for.
     * @return List of matching tasks.
     */
    public List<Task> findTasks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String lowerKeyword = keyword.trim().toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Returns the full list of tasks.
     *
     * @return List of all tasks.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Validates if a 1-based index is within bounds of the task list.
     *
     * @param index 1-based index to validate.
     * @throws BoboException If index is <= 0 or > list size.
     */
    private void validateIndex(int index) throws BoboException {
        if (index <= 0 || index > tasks.size()) {
            throw new BoboException("Task number " + index + " is out of bounds!");
        }
    }
}
