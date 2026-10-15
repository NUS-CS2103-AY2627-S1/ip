package luckynoslacky.luckytask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import luckynoslacky.luckyexception.LuckyNoStorageException;
import luckynoslacky.luckyexception.LuckyNoTaskLimitException;
import luckynoslacky.luckystorage.CsvSaver;

/**
 * Stores tasks entered by the user in memory.
 */
public class TaskMaster {
    private static final int DEFAULT_MAX_TASKS = 100;

    private final TaskList tasks;
    private final TaskSchedulingService taskSchedulingService;
    private final int maxTasks;
    private final CsvSaver csvSaver;

    /**
     * Creates a task master with the default capacity of 100 tasks.
     */
    public TaskMaster() {
        this(DEFAULT_MAX_TASKS);
    }

    /**
     * Creates a task master with a configurable capacity.
     *
     * @param maxTasks maximum number of tasks that can be stored
     */
    public TaskMaster(int maxTasks) {
        this(maxTasks, new CsvSaver());
    }

    /**
     * Creates a task master with the default capacity and a configurable saver.
     *
     * @param saver saver used after task-list mutations
     */
    public TaskMaster(CsvSaver saver) {
        this(DEFAULT_MAX_TASKS, saver);
    }

    /**
     * Creates a task master with a configurable capacity and saver.
     *
     * @param maxTasks maximum number of tasks that can be stored
     * @param saver saver used after task-list mutations
     */
    public TaskMaster(int maxTasks, CsvSaver saver) {
        if (maxTasks <= 0) {
            throw new IllegalArgumentException("Maximum tasks must be positive.");
        }

        if (saver == null) {
            throw new IllegalArgumentException("Saver cannot be null.");
        }

        this.maxTasks = maxTasks;
        this.csvSaver = saver;
        tasks = new TaskList();
        taskSchedulingService = new TaskSchedulingService(tasks);
    }

    /**
     * Adds a task to the task list.
     *
     * @param task task description entered by the user
     */
    public void addTask(Task task) {
        if (tasks.size() >= maxTasks) {
            throw new LuckyNoTaskLimitException("The task list is full.");
        }

        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        int previousTaskCount = tasks.size();
        tasks.addTask(task);
        assert tasks.size() == previousTaskCount + 1
                : "Task count did not increase after adding a task";
        assert tasks.size() <= maxTasks
                : "Task list exceeded its maximum capacity";
        saveChangesOrRollback(() -> tasks.removeTask(tasks.size()));
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return current task count
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns all stored tasks with their original one-based numbers.
     *
     * @return indexed task list
     */
    public TaskList listTasks() {
        return tasks.createView(null, task -> true);
    }

    /**
     * Searches tasks by description.
     *
     * @param descriptionQuery text to search for in task descriptions
     * @return indexed matching task list
     */
    public TaskList findTasks(String descriptionQuery) {
        return findTasks(descriptionQuery, null);
    }

    /**
     * Lists deadlines and events occurring on the queried date.
     *
     * @param dateTimeQuery date and time from the find command
     * @return indexed matching task list
     */
    public TaskList findTasks(LocalDateTime dateTimeQuery) {
        return findTasks(null, dateTimeQuery);
    }

    /**
     * Searches tasks using the supplied optional description and date filters.
     * When both filters are present, a task must satisfy both filters.
     *
     * @param descriptionQuery optional text to search for in task descriptions
     * @param dateTimeQuery optional date and time on which a task must occur
     * @return indexed matching task list
     */
    public TaskList findTasks(
            String descriptionQuery,
            LocalDateTime dateTimeQuery) {
        if (descriptionQuery != null && descriptionQuery.isBlank()) {
            throw new IllegalArgumentException("Search description cannot be blank.");
        }

        if (descriptionQuery == null && dateTimeQuery == null) {
            throw new IllegalArgumentException("Search query cannot be empty.");
        }

        LocalDate searchDate = dateTimeQuery == null
                ? null
                : dateTimeQuery.toLocalDate();

        Predicate<Task> matcher =
                createSearchMatcher(descriptionQuery, searchDate);

        return tasks.createView(searchDate, matcher);
    }

    /**
     * Marks a task as done.
     *
     * @param taskNumber one-based number of the task to mark
     * @return task that was marked
     */
    public Task markTaskDone(int taskNumber) {
        return updateTaskStatus(taskNumber, true);
    }

    /**
     * Marks a task as not done.
     *
     * @param taskNumber one-based number of the task to unmark
     * @return task that was unmarked
     */
    public Task unmarkTaskUndone(int taskNumber) {
        return updateTaskStatus(taskNumber, false);
    }

    /**
     * Extends the ending time of a deadline or event by the supplied amount.
     *
     * @param taskNumber one-based number of the task to snooze
     * @param amount amount by which to extend the ending time
     * @return the updated task
     * @throws IllegalArgumentException if the task is a ToDo or the amount is
     *                                  invalid
     * @throws LuckyNoStorageException if the updated list cannot be saved
     */
    public Task snoozeTaskBy(int taskNumber, DurationPeriod amount) {
        return updateEndTime(taskNumber, task -> task.snoozeBy(amount));
    }

    /**
     * Replaces the ending time of a deadline or event.
     *
     * @param taskNumber one-based number of the task to snooze
     * @param newEndTime replacement ending time
     * @return the updated task
     * @throws IllegalArgumentException if the task is a ToDo or the time is
     *                                  invalid
     * @throws LuckyNoStorageException if the updated list cannot be saved
     */
    public Task snoozeTaskTo(int taskNumber, LocalDateTime newEndTime) {
        return updateEndTime(
                taskNumber,
                task -> task.reschedule(
                        task.getTaskTimes().withEndTime(newEndTime)));
    }

    /**
     * Replaces the schedule of a deadline or event task.
     *
     * @param taskNumber one-based number of the task to reschedule
     * @param newTimes replacement task timing information
     * @return the updated task
     * @throws IllegalArgumentException if the task or timing information is
     *                                  invalid
     * @throws LuckyNoStorageException if the updated list cannot be saved
     */
    public Task rescheduleTask(
            int taskNumber,
            TaskTimes newTimes) {
        Task task = getTask(taskNumber);
        TaskTimes previousTimes = task.getTaskTimes();

        task.reschedule(newTimes);
        saveChangesOrRollback(() -> task.reschedule(previousTimes));
        return task;
    }

    /**
     * Returns the scheduling state needed to parse a timed-task command.
     *
     * @param taskNumber one-based number of the task
     * @return immutable task scheduling state
     */
    public TaskSchedule getTaskSchedule(int taskNumber) {
        return taskSchedulingService.getTaskSchedule(taskNumber);
    }

    /**
     * Validates that a task can have its schedule modified.
     *
     * @param taskNumber one-based number of the task
     * @throws TaskSchedulingException if the task is a ToDo
     */
    public void validateTimedTask(int taskNumber) {
        taskSchedulingService.validateTimedTask(taskNumber);
    }

    /**
     * Validates the result of extending a task's ending time.
     *
     * @param taskNumber one-based number of the task
     * @param amount duration by which to extend the ending time
     * @param currentTime current date and time for deadline validation
     * @throws TaskSchedulingException if the task or resulting time is invalid
     */
    public void validateSnoozeBy(
            int taskNumber,
            DurationPeriod amount,
            LocalDateTime currentTime) {
        taskSchedulingService.validateSnoozeBy(taskNumber, amount, currentTime);
    }

    /**
     * Validates a replacement ending time for a deadline or event.
     *
     * @param taskNumber one-based number of the task
     * @param endTime replacement ending time
     * @param currentTime current date and time for deadline validation
     * @throws TaskSchedulingException if the task or replacement time is invalid
     */
    public void validateSnoozeTo(
            int taskNumber,
            LocalDateTime endTime,
            LocalDateTime currentTime) {
        taskSchedulingService.validateSnoozeTo(taskNumber, endTime, currentTime);
    }

    /**
     * Creates validated replacement timing information for a task.
     *
     * @param taskNumber one-based number of the task
     * @param startTime replacement event start time, or {@code null} for a deadline
     * @param endTime replacement deadline or event end time
     * @param currentTime current date and time for deadline validation
     * @return validated timing information matching the task category
     * @throws TaskSchedulingException if the task or replacement schedule is invalid
     */
    public TaskTimes createRescheduledTimes(
            int taskNumber,
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime currentTime) {
        return taskSchedulingService.createRescheduledTimes(
                taskNumber, startTime, endTime, currentTime);
    }

    /**
     * Returns the type of a task identified by its one-based task number.
     *
     * @param taskNumber one-based number of the task
     * @return task type
     */
    public Task.TaskType getTaskType(int taskNumber) {
        return getTask(taskNumber).getTaskType();
    }

    /**
     * Returns the start time of an event task.
     *
     * @param taskNumber one-based number of the task
     * @return event start time
     * @throws IllegalArgumentException if the task is not an event
     */
    public LocalDateTime getTaskStartTime(int taskNumber) {
        TaskTimes times = getTask(taskNumber).getTaskTimes();
        if (times.hasStartTime()) {
            return times.getStartTime();
        }
        throw new IllegalArgumentException("Task is not an event.");
    }

    /**
     * Returns the ending time of a deadline or event task.
     *
     * @param taskNumber one-based number of the task
     * @return deadline or event ending time
     * @throws IllegalArgumentException if the task is a ToDo
     */
    public LocalDateTime getTaskEndTime(int taskNumber) {
        return getTask(taskNumber).getEndTime();
    }

    /**
     * Updates a task's completion status and persists the change.
     *
     * @param taskNumber one-based number of the task to update
     * @param shouldBeDone whether the task should be marked as done
     * @return updated task
     * @throws LuckyNoStorageException if the updated list cannot be saved
     */
    private Task updateTaskStatus(int taskNumber, boolean shouldBeDone) {
        Task task = getTask(taskNumber);
        boolean wasDone = task.isDone();

        if (shouldBeDone) {
            task.markAsDone();
        } else {
            task.unmarkAsUndone();
        }

        saveChangesOrRollback(() -> restoreTaskStatus(task, wasDone));
        return task;
    }

    /**
     * Deletes a task from the task list.
     *
     * @param taskNumber one-based number of the task to delete
     * @return deleted task
     */
    public Task deleteTask(int taskNumber) {
        int previousTaskCount = tasks.size();
        Task deletedTask = tasks.removeTask(taskNumber);
        assert tasks.size() == previousTaskCount - 1
                : "Task count did not decrease after deleting a task";
        saveChangesOrRollback(() -> tasks.insertTask(taskNumber, deletedTask));
        return deletedTask;
    }

    /**
     * Replaces the in-memory task list with tasks loaded from CSV storage.
     * This method does not save the list again.
     *
     * @param loadedTasks tasks loaded from CSV storage
     */
    public void loadTasksFromCsvStorageRecord(List<Task> loadedTasks) {
        validateLoadedTasks(loadedTasks);

        this.tasks.replaceTasks(loadedTasks);
    }

    /**
     * Creates a predicate for the supplied optional search filters.
     *
     * @param descriptionQuery optional description text
     * @param searchDate optional date on which a task must occur
     * @return predicate matching both supplied filters
     */
    private Predicate<Task> createSearchMatcher(
            String descriptionQuery,
            LocalDate searchDate) {
        return task -> {
            boolean matchesDescription = descriptionQuery == null
                    || task.matchesDescription(descriptionQuery);
            boolean matchesDate = searchDate == null
                    || task.occursOn(searchDate);
            return matchesDescription && matchesDate;
        };
    }

    /**
     * Saves the current task list and restores the previous state if saving
     * fails.
     *
     * @param rollbackAction action that restores the pre-save state
     * @throws LuckyNoStorageException if saving fails
     */
    private void saveChangesOrRollback(Runnable rollbackAction) {
        try {
            csvSaver.save(tasks);
        } catch (LuckyNoStorageException exception) {
            rollbackAction.run();
            throw exception;
        }
    }

    /**
     * Validates tasks loaded from persistent storage.
     *
     * @param loadedTasks tasks to validate
     * @throws LuckyNoStorageException if the list is null, too large, or
     *                                 contains a null task
     */
    private void validateLoadedTasks(List<Task> loadedTasks) {
        if (loadedTasks == null) {
            throw new LuckyNoStorageException("Tasks cannot be null.");
        }

        if (loadedTasks.size() > maxTasks) {
            throw new LuckyNoStorageException(
                    "Saved task list exceeds the maximum capacity.");
        }

        if (loadedTasks.stream().anyMatch(task -> task == null)) {
            throw new LuckyNoStorageException(
                    "Saved task list contains a null task.");
        }
    }

    /**
     * Applies an ending-time update and restores the previous ending time if
     * persistence fails.
     *
     * @param taskNumber one-based task number
     * @param updateAction operation that changes the task ending time
     * @return updated task
     */
    private Task updateEndTime(
            int taskNumber,
            Consumer<Task> updateAction) {
        Task task = getTask(taskNumber);
        TaskTimes previousTimes = task.getTaskTimes();

        updateAction.accept(task);

        saveChangesOrRollback(() -> task.reschedule(previousTimes));
        return task;
    }

    /**
     * Restores a task's status after a failed persistence operation.
     *
     * @param task task whose status should be restored
     * @param wasDone status before the attempted change
     */
    private void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.unmarkAsUndone();
        }
    }

    /**
     * Gets a task by its one-based task number.
     *
     * @param taskNumber one-based number of the task
     * @return the requested task
     */
    private Task getTask(int taskNumber) {
        return tasks.getTask(taskNumber);
    }
}
