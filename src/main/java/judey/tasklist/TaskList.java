package judey.tasklist;

import judey.exception.JudeyException;
import judey.task.Deadline;
import judey.task.Event;
import judey.task.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Manages the task list and provides operations to modify tasks. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to include in the list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the tasks currently stored in this task list.
     *
     * @return the stored tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the number of tasks in this task list.
     *
     * @return the number of stored tasks
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Add a new task to the list of tasks
     * @param task refers to the task to be added into the list
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Delete task from the list of tasks
     * @param index the task at this index would be deleted
     * @return the task that was removed
     * @throws JudeyException
     */
    public Task delete(int index) throws JudeyException {
        validateIndex(index);
        return tasks.remove(index);
    }

    /**
     * Marks a task as done
     * @param index task at this index of the list is to be marked as done
     * @return the task that is marked as done
     * @throws JudeyException if index is greater than the number of tasks in the list or is less than 0,
     * an exception is thrown in validateIndex method
     */
    public Task markAsDone(int index) throws JudeyException {
        validateIndex(index);
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks a task - task is not done
     * @param index task at this index of the list is to be marked as not done
     * @return the task that is marked as not done
     * @throws JudeyException if index is greater than the number of tasks in the list or is less than 0,
     * an exception is thrown in validateIndex method
     */
    public Task markAsNotDone(int index) throws JudeyException {
        validateIndex(index);
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Checks if the index is within valid range of the task list
     * @param index the index which is being validated
     * @throws JudeyException if the index is negative or greater than or equal to the total number of tasks
     */
    private void validateIndex(int index) throws JudeyException {
        if (index < 0 || index >= tasks.size()) {
            throw new JudeyException("I checked twice, but that task number is not on the list.");
        }
    }

    /**
     * Prints all tasks in the list sequentially with 1-based index numbering
     */
    public void printList() {
        System.out.println("Here are the tasks currently orbiting your mission log:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.print((index + 1) + "." + tasks.get(index));
        }
        System.out.println();
    }

    /**
     * Filters and prints all deadlines or events on the specified date.
     * If no matching tasks are found, a notification is printed instead.
     *
     * @param searchDate The target date to search for tasks
     */
    public void printTasksOnDate(LocalDate searchDate) {
        System.out.println("Mission tasks on " + searchDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ":");
        int count = 0;
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            boolean matches = false;
            if (task instanceof Deadline) {
                matches = ((Deadline) task).getBy().toLocalDate().equals(searchDate);
            } else if (task instanceof Event) {
                LocalDate startDate = ((Event) task).getFrom().toLocalDate();
                LocalDate endDate = ((Event) task).getTo().toLocalDate();
                matches = !searchDate.isBefore(startDate) && !searchDate.isAfter(endDate);
            }

            if (matches) {
                count++;
                System.out.print((index + 1) + "." + task);
            }
        }
        if (count == 0) {
            System.out.println(" No deadlines or events found in this orbit.");
        }
    }

    /**
     * Searches for tasks matching a keyword and prints formatted results.
     *
     * @param keyword search term to query against descriptions
     */
    public void findTasks(String keyword) {
        System.out.println("Here are the matching tasks in your mission log:");
        int matchCount = 0;
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchCount++;
                System.out.print((index + 1) + "." + task);
            }
        }
        if (matchCount == 0) {
            System.out.println("No matching tasks found in this sector.");
        }
    }
}
