package dukey.task;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Contains and manages the chatbot's task list.
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
     * Creates a task list backed by the given list of tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Returns the task at the given one-based task number.
     *
     * @param taskNumber One-based task number.
     * @return Matching task.
     */
    public Task get(int taskNumber) {
        assert isValidTaskNumber(taskNumber) : "Task number should be valid before getting a task.";
        return this.tasks.get(taskNumber - 1);
    }

    /**
     * Deletes the task at the given one-based task number.
     *
     * @param taskNumber One-based task number.
     * @return Deleted task.
     */
    public Task delete(int taskNumber) {
        assert isValidTaskNumber(taskNumber) : "Task number should be valid before deleting a task.";
        return this.tasks.remove(taskNumber - 1);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword Keyword to search for.
     * @return Task list containing matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        TaskList matchingTasks = new TaskList();

        for (Task task : this.tasks) {
            if (task.containsKeyword(keyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

    /**
     * Counts done tasks in the list.
     *
     * @return Number of done tasks.
     */
    public int countCompletedTasks() {
        int count = 0;
        for (Task task : this.tasks) {
            if (task.isDone()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts tasks that are not done.
     *
     * @return Number of pending tasks.
     */
    public int countPendingTasks() {
        return size() - countCompletedTasks();
    }

    /**
     * Counts tasks completed in the calendar week containing the given date.
     *
     * @param date Date whose calendar week should be checked.
     * @return Number of tasks completed in that calendar week.
     */
    public int countCompletedInCalendarWeek(LocalDate date) {
        int count = 0;
        for (Task task : this.tasks) {
            if (task.wasCompletedInCalendarWeek(date)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts tasks completed in the calendar month containing the given date.
     *
     * @param date Date whose calendar month should be checked.
     * @return Number of tasks completed in that calendar month.
     */
    public int countCompletedInCalendarMonth(LocalDate date) {
        int count = 0;
        for (Task task : this.tasks) {
            if (task.wasCompletedInCalendarMonth(date)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Checks whether the given one-based task number exists in the list.
     *
     * @param taskNumber One-based task number to check.
     * @return True if the task number points to an existing task.
     */
    public boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= size();
    }
}
