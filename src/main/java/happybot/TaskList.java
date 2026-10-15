package happybot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import happybot.task.Deadline;
import happybot.task.Event;
import happybot.task.Task;
import happybot.task.ToDo;

/**
 * Holds the tasks of one HappyBot session and the operations that change them.
 *
 * <p>A task is named from the outside by its number in the list, counted from one as it is
 * shown to the user. Turning that number into a position, and refusing a number that belongs
 * to no task, happens here, so no other class works out positions for itself.
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
     * Creates a task list holding the specified tasks.
     *
     * <p>The tasks are copied, so the list stays in charge of its own contents even when the
     * caller keeps a reference to the list it passed in.
     *
     * @param tasks The tasks to start with, usually the ones read from the data file.
     * @throws IllegalArgumentException If the supplied list has a duplicate task.
     */
    public TaskList(List<Task> tasks) {
        this();
        for (Task task : tasks) {
            if (containsTaskWithSameDetails(task)) {
                throw new IllegalArgumentException("A task list cannot contain duplicate tasks.");
            }

            this.tasks.add(task);
        }
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The task count.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns whether the list holds no tasks.
     *
     * @return True if there is nothing in the list.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks in list order, for displaying or saving them.
     *
     * @return A read-only view of the tasks.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param taskToAdd The task to add.
     * @throws HappyBotException If the task duplicates one already in the list.
     */
    public void add(Task taskToAdd) throws HappyBotException {
        if (containsTaskWithSameDetails(taskToAdd)) {
            throw new HappyBotException("That task is already in your list.");
        }

        tasks.add(taskToAdd);
    }

    /**
     * Returns the task holding the specified number.
     *
     * @param taskNumber The task number, counted from one.
     * @return The task holding that number.
     * @throws HappyBotException If no task holds that number.
     */
    public Task getTask(int taskNumber) throws HappyBotException {
        checkTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes the task holding the specified number and returns it.
     *
     * <p>The tasks after it move up, so their numbers each fall by one.
     *
     * @param taskNumber The task number, counted from one.
     * @return The task that was removed.
     * @throws HappyBotException If no task holds that number.
     */
    public Task delete(int taskNumber) throws HappyBotException {
        checkTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the deadlines due on the specified date, each under its task number.
     *
     * <p>The numbers are the ones the full list gives, so a deadline found here can be marked
     * or deleted by the number shown beside it.
     *
     * @param date The date the deadlines are wanted for.
     * @return The matching deadlines in list order, stored under their task numbers.
     */
    public Map<Integer, Deadline> findDeadlinesDueOn(LocalDate date) {
        // A LinkedHashMap keeps the deadlines in list order while remembering each task number.
        Map<Integer, Deadline> matchingDeadlines = new LinkedHashMap<>();

        for (int i = 0; i < tasks.size(); i++) {
            // A todo and an event have no due date, so only a deadline can match.
            if (tasks.get(i) instanceof Deadline deadline
                    && deadline.getDueDateTime().toLocalDate().equals(date)) {
                matchingDeadlines.put(i + 1, deadline);
            }
        }

        return matchingDeadlines;
    }

    /**
     * Returns the tasks whose description holds the specified keyword, each under its task number.
     *
     * <p>The search ignores capitalization, so "Book" finds a task described as "read book". The
     * numbers are the ones the full list gives, so a task found here can be marked or deleted by
     * the number shown beside it.
     *
     * @param keyword The text to look for in each description.
     * @return The matching tasks in list order, stored under their task numbers.
     */
    public Map<Integer, Task> findTasksContaining(String keyword) {
        // A LinkedHashMap keeps the tasks in list order while remembering each task number.
        Map<Integer, Task> matchingTasks = new LinkedHashMap<>();
        // Locale.ROOT keeps the lowercasing the same on every machine, unlike the default
        // locale, which lowercases the letter I differently in Turkish.
        String loweredKeyword = keyword.toLowerCase(Locale.ROOT);

        for (int i = 0; i < tasks.size(); i++) {
            String description = tasks.get(i).getDescription().toLowerCase(Locale.ROOT);
            if (description.contains(loweredKeyword)) {
                matchingTasks.put(i + 1, tasks.get(i));
            }
        }

        return matchingTasks;
    }

    /**
     * Returns the statistics for the Monday-to-Sunday week containing the specified date.
     *
     * @param currentDate A date in the week whose statistics are wanted.
     * @return The task counts for that week.
     */
    TaskStatistics getStatistics(LocalDate currentDate) {
        LocalDate weekStart = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        int completedTaskCount = 0;
        int completedToDoCount = 0;
        int completedDeadlineCount = 0;
        int completedEventCount = 0;
        int uncompletedDeadlineCount = 0;

        for (Task task : tasks) {
            if (isCompletedInWeek(task, weekStart, weekEnd)) {
                completedTaskCount++;
                if (task instanceof ToDo) {
                    completedToDoCount++;
                } else if (task instanceof Deadline) {
                    completedDeadlineCount++;
                } else if (task instanceof Event) {
                    completedEventCount++;
                }
            }

            if (task instanceof Deadline deadline
                    && !task.isDone()
                    && isDateInWeek(deadline.getDueDateTime().toLocalDate(), weekStart, weekEnd)) {
                uncompletedDeadlineCount++;
            }
        }

        return new TaskStatistics(tasks.size(), completedTaskCount, completedToDoCount,
                completedDeadlineCount, completedEventCount, uncompletedDeadlineCount);
    }

    /**
     * Returns whether a task is currently done and was completed during the given week.
     */
    private static boolean isCompletedInWeek(Task task, LocalDate weekStart, LocalDate weekEnd) {
        LocalDate completionDate = task.getCompletionDate();
        return task.isDone()
                && completionDate != null
                && isDateInWeek(completionDate, weekStart, weekEnd);
    }

    /**
     * Returns whether a date falls between the week's inclusive start and end dates.
     */
    private static boolean isDateInWeek(LocalDate date, LocalDate weekStart, LocalDate weekEnd) {
        return !date.isBefore(weekStart) && !date.isAfter(weekEnd);
    }

    /**
     * Rejects a task number that belongs to no task.
     *
     * @param taskNumber The task number, counted from one.
     * @throws HappyBotException If no task holds that number.
     */
    private void checkTaskNumber(int taskNumber) throws HappyBotException {
        boolean isTaskNumberInRange = taskNumber >= 1 && taskNumber <= tasks.size();
        if (!isTaskNumberInRange) {
            throw new HappyBotException("Please choose a valid task number.");
        }

        assert isTaskNumberInRange : "A task number must identify an existing task.";
    }

    /**
     * Returns whether the list already holds a task with the same user-facing details.
     */
    private boolean containsTaskWithSameDetails(Task taskToCheck) {
        for (Task task : tasks) {
            if (task.hasSameDetails(taskToCheck)) {
                return true;
            }
        }

        return false;
    }
}
