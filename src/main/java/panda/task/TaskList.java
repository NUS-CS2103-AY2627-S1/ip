package panda.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import panda.exception.InvalidDateException;
import panda.exception.InvalidTaskNumberException;
import panda.exception.InvalidUpdateException;

/**
 * Owns Panda's ordered collection of tasks and all operations on that list.
 * Command-processing code can work with one-based task numbers without
 * depending directly on {@link ArrayList} indexing details.
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
     * Creates a task list containing tasks loaded from storage.
     *
     * @param initialTasks the tasks in their stored order.
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Marks the identified task as completed.
     *
     * @param taskNumber the one-based task number.
     * @return the task that was marked.
     * @throws InvalidTaskNumberException if no task has that number.
     */
    public Task mark(int taskNumber) throws InvalidTaskNumberException {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Marks the identified task as unfinished.
     *
     * @param taskNumber the one-based task number.
     * @return the task that was unmarked.
     * @throws InvalidTaskNumberException if no task has that number.
     */
    public Task unmark(int taskNumber) throws InvalidTaskNumberException {
        Task task = getTask(taskNumber);
        task.unmark();
        return task;
    }

    /**
     * Changes the name of the identified task.
     *
     * @param taskNumber the one-based task number.
     * @param updatedName the validated replacement name.
     * @return the renamed task.
     * @throws InvalidTaskNumberException if no task has that number.
     */
    public Task rename(int taskNumber, String updatedName)
            throws InvalidTaskNumberException {
        Task task = getTask(taskNumber);
        task.rename(updatedName);
        return task;
    }

    /**
     * Changes the deadline of the identified Deadline task.
     *
     * @param taskNumber the one-based task number.
     * @param updatedDateTimeText the replacement deadline date and time.
     * @return the rescheduled Deadline.
     * @throws InvalidTaskNumberException if no task has that number.
     * @throws InvalidUpdateException if the selected task is not a Deadline.
     * @throws InvalidDateException if the replacement is not a valid date and time.
     */
    public Task rescheduleDeadline(int taskNumber, String updatedDateTimeText)
            throws InvalidTaskNumberException, InvalidUpdateException, InvalidDateException {
        Task task = getTask(taskNumber);
        if (!(task instanceof Deadline deadline)) {
            throw InvalidUpdateException.createForUnsupportedTiming();
        }
        deadline.reschedule(updatedDateTimeText);
        return deadline;
    }

    /**
     * Changes the complete time interval of the identified Event task.
     *
     * @param taskNumber the one-based task number.
     * @param updatedStartDateTimeText the replacement starting date and time.
     * @param updatedEndDateTimeText the replacement ending date and time.
     * @return the rescheduled Event.
     * @throws InvalidTaskNumberException if no task has that number.
     * @throws InvalidUpdateException if the selected task is not an Event.
     * @throws InvalidDateException if either replacement is not a valid date and time.
     */
    public Task rescheduleEvent(int taskNumber, String updatedStartDateTimeText,
            String updatedEndDateTimeText)
            throws InvalidTaskNumberException, InvalidUpdateException, InvalidDateException {
        Task task = getTask(taskNumber);
        if (!(task instanceof Event event)) {
            throw InvalidUpdateException.createForUnsupportedTiming();
        }
        event.reschedule(updatedStartDateTimeText, updatedEndDateTimeText);
        return event;
    }

    /**
     * Removes the identified task from the list.
     *
     * @param taskNumber the one-based task number.
     * @return the removed task.
     * @throws InvalidTaskNumberException if no task has that number.
     */
    public Task delete(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns all tasks together with their stable one-based numbers.
     *
     * @return the numbered tasks in list order.
     */
    public List<NumberedTask> getTasks() {
        return createNumberedTasks(null);
    }

    /**
     * Returns dated tasks that occur on the supplied date.
     * Date-free to-dos are not included.
     *
     * @param date the date used to filter tasks.
     * @return matching tasks with their numbers from the complete list.
     */
    public List<NumberedTask> getTasksOn(LocalDate date) {
        return createNumberedTasks(date);
    }

    /**
     * Returns tasks whose names contain the supplied search term.
     * Matching ignores letter case and preserves numbers from the complete list.
     *
     * @param searchTerm the text to find in task names
     * @return matching tasks with their original one-based numbers
     */
    public List<NumberedTask> getTasksMatching(String searchTerm) {
        String normalizedSearchTerm = searchTerm.toLowerCase(Locale.ROOT);
        ArrayList<NumberedTask> matchingTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String normalizedTaskName = task.getName().toLowerCase(Locale.ROOT);
            if (normalizedTaskName.contains(normalizedSearchTerm)) {
                matchingTasks.add(new NumberedTask(i + 1, task));
            }
        }
        return List.copyOf(matchingTasks);
    }

    /**
     * Returns a read-only snapshot suitable for saving.
     *
     * @return the tasks in list order.
     */
    public List<Task> getTaskSnapshot() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the task count.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Resolves a one-based task number to its task object.
     */
    private Task getTask(int taskNumber) throws InvalidTaskNumberException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Ensures a one-based task number identifies an existing task.
     */
    private void validateTaskNumber(int taskNumber) throws InvalidTaskNumberException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new InvalidTaskNumberException(taskNumber);
        }
    }

    /**
     * Builds display entries while preserving numbers from the complete list.
     * A null filter selects every task.
     */
    private List<NumberedTask> createNumberedTasks(LocalDate filterDate) {
        ArrayList<NumberedTask> numberedTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (filterDate == null || task.occursOn(filterDate)) {
                numberedTasks.add(new NumberedTask(i + 1, task));
            }
        }
        return List.copyOf(numberedTasks);
    }

    /**
     * Pairs a task with its one-based position in the complete task list.
     *
     * @param number the one-based task number.
     * @param task the corresponding task.
     */
    public record NumberedTask(int number, Task task) {
    }
}
