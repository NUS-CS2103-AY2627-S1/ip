package probe.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import probe.ProbeException;

/**
 * Owns the tasks and provides operations for changing the task list.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>(); 
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks Tasks to copy into the list.
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
        tasks.add(task); 
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size(); 
    }

    /**
     * Returns a copy of the tasks in their current order.
     *
     * @return Copy of the tasks.
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks); 
    }

    /**
     * Returns the task at a one-based task number.
     *
     * @param number One-based task number.
     * @return Task at the specified number.
     * @throws ProbeException If the task number is invalid.
     */
    public Task get(int number) throws ProbeException {
        validateTaskNumber(number);
        Task task = tasks.get(number - 1);
        assert task != null : "A valid task number must refer to a task";
        return task;
    }

    /**
     * Removes and returns the task at a one-based task number.
     *
     * @param number One-based task number.
     * @return Removed task.
     * @throws ProbeException If the task number is invalid.
     */
    public Task delete(int number) throws ProbeException {
        validateTaskNumber(number);
        Task removedTask = tasks.remove(number - 1);
        assert removedTask != null : "A valid task number must refer to a task";
        return removedTask;
    }
    /**
     * Validates that a one-based task number refers to an existing task.
     *
     * @param number One-based task number to validate.
     * @throws ProbeException If the task number is invalid.
     */
    private void validateTaskNumber(int number) throws ProbeException {
        if (number < 1 || number > tasks.size()) {
            throw new ProbeException("That task number does not exist.");
        }
        assert number >= 1 && number <= tasks.size()
                : "Task number validation must establish a valid one-based index";
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to search for.
     * @return New task list containing matching tasks.
     */
    public TaskList search(String keyword) {
        String searchKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(searchKeyword))
                .toList();
        return new TaskList(matchingTasks);
    }
}
