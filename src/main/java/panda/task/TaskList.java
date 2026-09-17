package panda.task;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import panda.exception.task.EventClashException;
import panda.exception.task.InvalidEventParametersException;
import panda.exception.task.InvalidTaskListIndexException;
import panda.exception.task.TaskAlreadyMarkedException;
import panda.exception.task.TaskAlreadyUnmarkedException;
import panda.util.datetime.DateTimeHelper;

/**
 * Stores tasks loaded for the current Panda session and provides operations for managing them.
 */
public final class TaskList {
    private final EventScheduleIndex eventScheduleIndex;
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
        this.eventScheduleIndex = new EventScheduleIndex(tasks);
    }

    /**
     * Creates and adds a to-do task.
     *
     * @param description Description of the task.
     * @return The newly added task.
     */
    public Task addTodo(String description) {
        return appendToList(new Todo(description));
    }

    /**
     * Creates and adds a deadline task.
     *
     * @param description Description of the task.
     * @param dueDate Date by which the task is due as a {@link Temporal}.
     * @return The newly added task.
     */
    public Task addDeadline(String description, Temporal dueDate) {
        return appendToList(new Deadline(description, dueDate));
    }

    /**
     * Creates and adds an event task.
     *
     * @param description Description of the event.
     * @param startDateTime Event start date and time as a {@link Temporal}.
     * @param endDateTime Event end date and time as a {@link Temporal}.
     * @return The newly added task.
     * @throws EventClashException If the event overlaps an existing event.
     * @throws InvalidEventParametersException If the event does not end after it starts.
     */
    public Task addEvent(String description, Temporal startDateTime, Temporal endDateTime)
            throws EventClashException, InvalidEventParametersException {
        return addTask(new Event(description, startDateTime, endDateTime));
    }

    /**
     * Returns the number of tasks currently in this list.
     *
     * @return Task count.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks currently in this list.
     *
     * @return Snapshot of the current tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Adds an already-created task, checking its schedule when it is an event.
     *
     * @param task Task to add.
     * @return Added task.
     * @throws EventClashException If the task is an event that overlaps an existing event.
     */
    public Task addTask(Task task) throws EventClashException {
        assert task != null : "Task added to TaskList must not be null";

        if (task instanceof Event event) {
            eventScheduleIndex.add(event);
        }
        return appendToList(task);
    }

    private Task appendToList(Task task) {
        tasks.add(task);
        return task;
    }

    /**
     * Converts a displayed task number to a valid zero-based index.
     *
     * @param taskNumber One-based task number entered by the user.
     * @return Zero-based index of the identified task.
     * @throws InvalidTaskListIndexException If the number does not identify a task in this list.
     */
    private int getValidatedIndex(int taskNumber) throws InvalidTaskListIndexException {
        int arrayIndex = taskNumber - 1;
        if (arrayIndex < 0 || arrayIndex >= tasks.size()) {
            throw new InvalidTaskListIndexException(taskNumber, tasks.size());
        }
        return arrayIndex;
    }

    /**
     * Marks the task with the supplied one-based task number.
     *
     * @param taskNumber Number displayed beside the task.
     * @return The task that was marked.
     * @throws InvalidTaskListIndexException If the number does not identify a task in this list.
     * @throws TaskAlreadyMarkedException If the task is already marked.
     */
    public Task markTask(int taskNumber) throws TaskAlreadyMarkedException, InvalidTaskListIndexException {
        int arrayIndex = getValidatedIndex(taskNumber);

        Task task = tasks.get(arrayIndex);
        if (task.isMarked()) {
            throw new TaskAlreadyMarkedException(task);
        }
        task.mark();
        return task;
    }

    /**
     * Removes the mark from the task with the supplied one-based task number.
     *
     * @param taskNumber Number displayed beside the task.
     * @return The task that was unmarked.
     * @throws InvalidTaskListIndexException If the number does not identify a task in this list.
     * @throws TaskAlreadyUnmarkedException If the task is already unmarked.
     */
    public Task unmarkTask(int taskNumber) throws TaskAlreadyUnmarkedException, InvalidTaskListIndexException {
        int arrayIndex = getValidatedIndex(taskNumber);

        Task task = tasks.get(arrayIndex);
        if (!task.isMarked()) {
            throw new TaskAlreadyUnmarkedException(task);
        }
        task.unmark();
        return task;
    }

    /**
     * Removes the task with the supplied one-based task number.
     *
     * @param taskNumber Number displayed beside the task.
     * @return The removed task.
     * @throws InvalidTaskListIndexException If the number does not identify a task in this list.
     */
    public Task delete(int taskNumber) throws InvalidTaskListIndexException {
        int arrayIndex = getValidatedIndex(taskNumber);
        Task deletedTask = tasks.remove(arrayIndex);
        if (deletedTask instanceof Event event) {
            eventScheduleIndex.remove(event);
        }
        return deletedTask;
    }

    /**
     * Returns a numbered, multi-line string representation of all tasks that occur on,
     * are due on, or span across the specified date, using each task's original 1-based index in the list.
     *
     * @param date Date to filter tasks by as a {@link Temporal}.
     * @return Formatted list of matching tasks with original list numbers, or an empty string if none match.
     */
    public String getTasksOnDate(Temporal date) {
        if (date == null) {
            return "";
        }
        String heading = "Today's attempt at productivity for "
                + DateTimeHelper.formatForDisplay(date) + ":";
        return formatMatchingTasks(task -> task.occursOn(date), heading, "");
    }

    /**
     * Returns a numbered, multi-line string representation of tasks whose descriptions contain the whole keyword,
     * using each task's original one-based index in the list.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Formatted list of matching tasks with original list numbers, or an empty list message if none match.
     */
    public String getTasksWithKeyword(String keyword) {
        return formatMatchingTasks(task -> task.hasKeyword(keyword),
                "I found them. You're welcome; that was apparently too hard:",
                "No matches. Even the task is avoiding you.");
    }

    /**
     * Returns all tasks as a numbered, multi-line string.
     *
     * @return The formatted task list.
     */
    @Override
    public String toString() {
        return formatMatchingTasks(task -> true,
                "Behold your path to kungfu mastery:",
                "Your path is empty. Kungfu mastery remains safely out of reach.");
    }

    /**
     * Formats tasks that satisfy the supplied condition, retaining their original task numbers.
     *
     * @param taskCondition Condition used to select tasks for display.
     * @param heading Heading displayed before matching tasks.
     * @param noMatchesMessage Message returned when no tasks match.
     * @return Formatted matching tasks, or the supplied no-match message when none match.
     */
    private String formatMatchingTasks(Predicate<Task> taskCondition, String heading, String noMatchesMessage) {
        StringBuilder result = new StringBuilder(heading).append(System.lineSeparator());
        boolean hasMatches = false;
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (taskCondition.test(task)) {
                hasMatches = true;
                result.append(index + 1)
                        .append(".")
                        .append(task)
                        .append(System.lineSeparator());
            }
        }
        if (!hasMatches) {
            return noMatchesMessage;
        }
        return result.toString().stripTrailing();
    }
}
