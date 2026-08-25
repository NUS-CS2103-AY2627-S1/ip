package matthew.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import matthew.exception.MatthewException;

/** Stores and manages an ordered collection of tasks. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks in display order.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
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
     * Deletes and returns the task with the specified one-based number.
     *
     * @param taskNumber One-based number of the task to delete.
     * @return Deleted task.
     * @throws MatthewException If the task number is outside the list.
     */
    public Task delete(int taskNumber)
            throws MatthewException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks and returns the task with the specified one-based number as done.
     *
     * @param taskNumber One-based number of the task to mark.
     * @return Marked task.
     * @throws MatthewException If the task number is outside the list.
     */
    public Task mark(int taskNumber)
            throws MatthewException {
        Task task = get(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks and returns the task with the specified one-based number as not done.
     *
     * @param taskNumber One-based number of the task to unmark.
     * @return Unmarked task.
     * @throws MatthewException If the task number is outside the list.
     */
    public Task unmark(int taskNumber)
            throws MatthewException {
        Task task = get(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the task with the specified one-based number.
     *
     * @param taskNumber One-based number of the task to retrieve.
     * @return Task at the specified number.
     * @throws MatthewException If the task number is outside the list.
     */
    public Task get(int taskNumber)
            throws MatthewException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
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
     * Returns a shallow copy of the tasks in their current order.
     *
     * @return Copy of the tasks in list order.
     */
    public ArrayList<Task> getAll() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns deadlines and events that occur on the specified date.
     *
     * @param date Date on which to find tasks.
     * @return Matching deadlines and events in list order.
     */
    public ArrayList<Task> getTasksOn(LocalDate date) {
        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task instanceof Deadline
                    && ((Deadline) task).occursOn(date)) {
                matchingTasks.add(task);
            } else if (task instanceof Event
                    && ((Event) task).occursOn(date)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword,
     * ignoring differences in letter case.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in list order.
     */
    public ArrayList<Task> getTasksContaining(String keyword) {
        String normalisedKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT)
                    .contains(normalisedKeyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

    /**
     * Validates that a one-based task number refers to an existing task.
     *
     * @param taskNumber Task number to validate.
     * @throws MatthewException If the task number is outside the list.
     */
    private void validateTaskNumber(int taskNumber)
            throws MatthewException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MatthewException(
                    "There is no task numbered " + taskNumber + ".");
        }
    }
}
