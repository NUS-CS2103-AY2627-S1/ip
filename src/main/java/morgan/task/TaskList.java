package morgan.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import morgan.exception.MorganException;

/**
 * Represents the list of tasks in Morgan application.
 * Manages operations on tasks such as adding, deleting, marking and finding.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Constructs an empty task list,
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with an existing list of task.
     *
     * @param tasks A list of Task objects to populate the list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Task list should not be null";
        assert !tasks.contains(null) : "Task list should not contain null tasks";
        this.tasks = tasks;
    }

    /**
     * Gets the contents of the taskList.
     *
     * @return The tasks in the list.
     */
    public List<Task> getTasks() {
        return this.tasks;
    }

    /**
     * Gets the size of the list.
     *
     * @return The index of the size.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Gets the task at the specific position.
     *
     * @param index The index of the position.
     * @return The task at the position.
     * @throws MorganException If the index is invalid.
     */
    public Task get(int index) throws MorganException {
        if (index < 0 || index >= tasks.size()) {
            throw new MorganException("Meow~ That fish doesn't exist in our tank!");
        }
        return tasks.get(index);
    }

    /**
     * Adds a new task in the task list.
     *
     * @param task The task object to be added.
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        tasks.add(task);
    }

    /**
     * Deletes specific task from the list.
     *
     * @param index The position of the task.
     * @return The task that is removed.
     * @throws MorganException If the index is invalid.
     */
    public Task delete(int index) throws MorganException {
        Task removed = get(index);
        tasks.remove(index);
        return removed;
    }

    /**
     * Marks the specific task as done.
     *
     * @param index The position of the task.
     * @return The task that is marked.
     * @throws MorganException If the index is invalid.
     */
    public Task mark(int index) throws MorganException {
        Task task = get(index);
        task.mark();
        return task;
    }

    /**
     * Marks the specific task as undone.
     *
     * @param index The position of the task.
     * @return The task that is unmarked.
     * @throws MorganException If the index is invalid.
     */
    public Task unmark(int index) throws MorganException {
        Task task = get(index);
        task.unmark();
        return task;
    }

    /**
     * Sorts tasks alphabetically by their descriptions, ignoring letter case.
     */
    public void sortByName() {
        tasks.sort(Comparator.comparing(Task::getName, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Finds the tasks happen on specific date.
     *
     * @param date The date entered by the user.
     * @return The tasks happen on the date.
     */
    public List<Task> findTasksOnDate(LocalDate date) {
        assert date != null : "Search date should not be null";
        return tasks.stream()
                .filter(task -> task.isOnDate(date))
                .collect(Collectors.toList());
    }

    /**
     * Finds and returns a list of tasks whose descriptions contain the specified keyword.
     *
     * @param keyword The substring to search for in task descriptions.
     * @return A list of tasks matching the search keyword.
     */
    public List<Task> findTaskWithKeyword(String keyword) {
        assert keyword != null : "Search keyword should not be null";
        return tasks.stream()
                .filter(task -> task.getName().contains(keyword))
                .collect(Collectors.toList());
    }
}
