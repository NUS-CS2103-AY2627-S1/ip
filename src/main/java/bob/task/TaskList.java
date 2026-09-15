package bob.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bob.exception.BobException;

/**
 * Stores and manages the tasks known to Bob.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list containing the supplied individual tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     * @throws BobException If the task list is full.
     */
    public void addTask(Task task) throws BobException {
        if (tasks.size() >= MAX_TASKS) {
            throw new BobException("Task list is full :( delete tasks to add more!");
        }
        tasks.add(task);
    }

    /**
     * Marks the task at the displayed one-based number as completed.
     *
     * @param taskNumber One-based displayed number of the task.
     * @return Task that was marked as completed.
     * @throws BobException If the task number does not identify a task.
     */
    public Task markTask(int taskNumber) throws BobException {
        validateTaskNumber(taskNumber);
        Task task = tasks.get(taskNumber - 1);
        task.markTask();
        return task;
    }

    /**
     * Marks the task at the displayed one-based number as incomplete.
     *
     * @param taskNumber One-based displayed number of the task.
     * @return Task that was marked as incomplete.
     * @throws BobException If the task number does not identify a task.
     */
    public Task unmarkTask(int taskNumber) throws BobException {
        validateTaskNumber(taskNumber);
        Task task = tasks.get(taskNumber - 1);
        task.unmarkTask();
        return task;
    }

    /**
     * Deletes the task at the displayed one-based number.
     *
     * @param taskNumber One-based displayed number of the task.
     * @throws BobException If the task number does not identify a task.
     */
    public void deleteTask(int taskNumber) throws BobException {
        validateTaskNumber(taskNumber);
        tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the task at a user-visible task number.
     *
     * @param taskNumber One-based number of the task to return.
     * @return Task at the specified task number.
     * @throws BobException If the task number is outside the task list.
     */
    public Task getTask(int taskNumber) throws BobException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Replaces the task at a user-visible task number.
     *
     * @param taskNumber One-based number of the task to replace.
     * @param replacementTask Task to store at the specified position.
     * @throws BobException If the task number is outside the task list.
     */
    public void replaceTask(int taskNumber, Task replacementTask) throws BobException {
        validateTaskNumber(taskNumber);
        tasks.set(taskNumber - 1, replacementTask);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of tasks in this list.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns a copy of the tasks in this list.
     *
     * @return Copy of the current tasks.
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns tasks with descriptions containing the specified keyword.
     * Matching is case-insensitive and preserves the tasks' displayed order.
     *
     * @param keyword Keyword to search for.
     * @return Tasks whose descriptions contain the keyword.
     */
    public List<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.hasKeywordInDescription(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Validates that a task number identifies a task in this list.
     *
     * @param taskNumber One-based displayed number of the task.
     * @throws BobException If the task number does not identify a task.
     */
    private void validateTaskNumber(int taskNumber) throws BobException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BobException("Please enter a valid index.");
        }
    }
}
