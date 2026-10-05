package friday.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import friday.FridayException;

/**
 * Stores the task list and provides operations that modify it.
 */
public class TaskList {
    private static final Comparator<Task> REMINDER_DATE_TIME_COMPARATOR = Comparator
            .comparing((Task task) -> task.getReminderDateTime().orElse(LocalDateTime.MAX))
            .thenComparing(Task::getDescription);

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list with the given tasks.
     *
     * @param tasks initial tasks in the task list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "TaskList should not store null tasks";

        tasks.add(task);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param taskIndex zero-based index of the task to mark
     * @return task that was marked
     * @throws FridayException if the index is outside the task list
     */
    public Task mark(int taskIndex) throws FridayException {
        Task task = get(taskIndex);
        if (!task.isDone()) {
            Optional<Task> next = task.nextOccurrence();
            if (next.isPresent()) {
                tasks.add(next.get());
                task.clearRepeatFrequency();
            }
        }
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param taskIndex zero-based index of the task to unmark
     * @return task that was unmarked
     * @throws FridayException if the index is outside the task list
     */
    public Task unmark(int taskIndex) throws FridayException {
        Task task = get(taskIndex);
        task.markAsNotDone();
        return task;
    }

    /**
     * Deletes and returns the task at the given zero-based index.
     *
     * @param taskIndex zero-based index of the task to delete
     * @return task that was deleted
     * @throws FridayException if the index is outside the task list
     */
    public Task delete(int taskIndex) throws FridayException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new FridayException("Apologies, that task number is not in the list sir");
        }
        return tasks.remove(taskIndex);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param taskIndex zero-based index of the task to return
     * @return task at the given index
     * @throws FridayException if the index is outside the task list
     */
    public Task get(int taskIndex) throws FridayException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new FridayException("Apologies, that task number is not in the list sir");
        }
        return tasks.get(taskIndex);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to find
     * @return task list containing matching tasks
     */
    public TaskList find(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.containsKeyword(keyword)) {
                matchingTasks.add(task);
            }
        }
        return new TaskList(matchingTasks);
    }

    /**
     * Sorts dated tasks chronologically, with tasks that have no date placed last.
     */
    public void sortByReminderDateTime() {
        tasks.sort(REMINDER_DATE_TIME_COMPARATOR);
    }

    /**
     * Marks an existing task as recurring.
     *
     * @param taskIndex zero-based index of the task
     * @param repeatFrequency frequency to assign
     * @return task that was marked as recurring
     * @throws FridayException if the index is outside the task list
     */
    public Task setRepeatFrequency(int taskIndex, RepeatFrequency repeatFrequency) throws FridayException {
        Task task = get(taskIndex);
        if (task.isDone()) {
            throw new FridayException("Please unmark the task before setting its repeat frequency.");
        }
        task.setRepeatFrequency(repeatFrequency);
        return task;
    }

    /**
     * Returns dated tasks sorted chronologically.
     *
     * @return task list containing only tasks that have reminders
     */
    public TaskList getTasksWithReminders() {
        ArrayList<Task> tasksWithReminders = new ArrayList<>();
        for (Task task : tasks) {
            if (!task.isDone() && task.getReminderDateTime().isPresent()) {
                tasksWithReminders.add(task);
            }
        }
        tasksWithReminders.sort(REMINDER_DATE_TIME_COMPARATOR);
        return new TaskList(tasksWithReminders);
    }

    /**
     * Returns tasks with reminder dates within the given period.
     *
     * @param start start of the reminder period
     * @param end end of the reminder period
     * @return task list containing reminders in the given period
     */
    public TaskList getUpcomingReminders(LocalDateTime start, LocalDateTime end) {
        ArrayList<Task> upcomingReminders = new ArrayList<>();
        for (Task task : tasks) {
            Optional<LocalDateTime> reminderDateTime = task.getReminderDateTime();
            if (!task.isDone() && reminderDateTime.isPresent()
                    && !reminderDateTime.get().isBefore(start)
                    && !reminderDateTime.get().isAfter(end)) {
                upcomingReminders.add(task);
            }
        }
        upcomingReminders.sort(REMINDER_DATE_TIME_COMPARATOR);
        return new TaskList(upcomingReminders);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks as a read-only list.
     *
     * @return read-only view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
