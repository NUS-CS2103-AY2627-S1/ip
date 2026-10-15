package eva;

import java.util.ArrayList;
import java.util.stream.Collectors;

import eva.task.Deadline;
import eva.task.Task;

/**
 * Stores and manages the tasks known to Eva.
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
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task list should not be null";
        this.tasks = tasks;
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
     * Returns the task at the specified zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at the specified index.
     */
    public Task getTaskAt(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task index should be within the task list";
        return tasks.get(index);
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        tasks.add(task);
    }

    /**
     * Deletes the task with the specified one-based task number.
     *
     * @param taskNumber One-based task number.
     * @return Deleted task.
     * @throws EvaException If the task number does not exist.
     */
    public Task delete(int taskNumber) throws EvaException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks the specified task as completed.
     *
     * @param taskNumber One-based task number.
     * @return Task that was marked.
     * @throws EvaException If the task number does not exist.
     */
    public Task mark(int taskNumber) throws EvaException {
        validateTaskNumber(taskNumber);
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the specified task as not completed.
     *
     * @param taskNumber One-based task number.
     * @return Task that was unmarked.
     * @throws EvaException If the task number does not exist.
     */
    public Task unmark(int taskNumber) throws EvaException {
        validateTaskNumber(taskNumber);
        Task task = tasks.get(taskNumber - 1);
        task.markAsNotDone();
        return task;
    }

    /**
     * Finds tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Task list containing the matching tasks.
     */
    public TaskList find(String keyword) {
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));

        return new TaskList(matchingTasks);
    }

    void sort() {
        tasks.sort((first, second) -> {
            if (first instanceof Deadline firstDeadline
                    && second instanceof Deadline secondDeadline) {
                return firstDeadline.getBy().compareTo(secondDeadline.getBy());
            }
            if (first instanceof Deadline) {
                return -1;
            }
            if (second instanceof Deadline) {
                return 1;
            }
            return 0;
        });
    }

    private void validateTaskNumber(int taskNumber) throws EvaException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EvaException("That task number does not exist.");
        }
    }
}
