package nano.task;

import java.util.ArrayList;
import java.util.List;

import nano.NanoException;

/**
 * Represents the list of tasks managed by Nano.
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
     * Creates a task list containing the given tasks.
     *
     * @param tasks tasks to add to the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the specified position.
     *
     * @param index index of the task.
     * @return the task at the specified position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task corresponding to the given task number.
     *
     * @param taskNumber user-visible task number, starting from 1.
     * @return the task at the specified task number.
     * @throws NanoException if the task number is invalid or out of range.
     */
    public Task getTask(int taskNumber) throws NanoException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new NanoException("That task number does not exist.");
        }

        return tasks.get(taskNumber - 1);
    }

    /**
     * Deletes the task corresponding to the given task number.
     *
     * @param taskNumber user-visible task number, starting from 1.
     * @return the deleted task.
     * @throws NanoException if the task number is invalid or out of range.
     */
    public Task deleteTask(int taskNumber) throws NanoException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new NanoException("That task number does not exist.");
        }

        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns all tasks in the list.
     *
     * @return the list of tasks.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to search for.
     * @return a task list containing all matching tasks.
     */
    public TaskList find(String keyword) {
        assert keyword != null;

        String searchTerm = keyword.toLowerCase();

        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase()
                        .contains(searchTerm))
                .toList();

        return new TaskList(matchingTasks);
    }
}
