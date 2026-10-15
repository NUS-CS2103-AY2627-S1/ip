package kojispawn.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kojispawn.exception.KojisPawnException;

/**
 * Owns and manages the tasks in Koji's Pawn.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks Tasks to place in the list.
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
     * Returns a read-only view for displaying the tasks.
     *
     * @return Tasks in their current order.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an independent copy of this task list and its task states.
     *
     * @return Copy of the current task list.
     */
    public TaskList copy() {
        return new TaskList(tasks.stream()
                .map(Task::copy)
                .toList());
    }

    /**
     * Replaces the current tasks with independent copies from another task list.
     *
     * @param source Task list to copy from.
     */
    public void replaceWith(TaskList source) {
        tasks.clear();
        source.tasks.stream()
                .map(Task::copy)
                .forEach(tasks::add);
    }

    /**
     * Finds tasks that occur on the given date.
     *
     * @param date Date to search for.
     * @return Matching tasks in their original order.
     */
    public List<Task> findOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword Keyword to find.
     * @return Matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.matches(keyword))
                .toList();
    }

    /**
     * Marks the selected task as complete.
     *
     * @param taskNumber One-based task number.
     * @return Task whose status changed.
     * @throws KojisPawnException If the task number is outside the list.
     */
    public Task mark(int taskNumber) throws KojisPawnException {
        Task task = getTask(taskNumber, "mark");
        task.markAsDone();
        return task;
    }

    /**
     * Marks the selected task as incomplete.
     *
     * @param taskNumber One-based task number.
     * @return Task whose status changed.
     * @throws KojisPawnException If the task number is outside the list.
     */
    public Task unmark(int taskNumber) throws KojisPawnException {
        Task task = getTask(taskNumber, "unmark");
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes the selected task.
     *
     * @param taskNumber One-based task number.
     * @return Removed task.
     * @throws KojisPawnException If the task number is outside the list.
     */
    public Task delete(int taskNumber) throws KojisPawnException {
        validateTaskNumber(taskNumber, "delete");
        return tasks.remove(taskNumber - 1);
    }

    private Task getTask(int taskNumber, String action) throws KojisPawnException {
        validateTaskNumber(taskNumber, action);
        return tasks.get(taskNumber - 1);
    }

    private void validateTaskNumber(int taskNumber, String action) throws KojisPawnException {
        if (taskNumber <= 0) {
            throw new KojisPawnException("The list begins at 1. Choose a positive task number.");
        }
        if (tasks.isEmpty()) {
            throw new KojisPawnException("There are no tasks to " + action + " yet.");
        }
        if (taskNumber > tasks.size()) {
            throw new KojisPawnException(
                    "No task occupies that position. Choose a number from 1 to " + tasks.size() + ".");
        }
    }
}
