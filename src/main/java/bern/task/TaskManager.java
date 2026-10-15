package bern.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import bern.datetime.DateTime;
import bern.logic.Response;
import bern.logic.ScheduleResponse;
import bern.logic.TaskResponse;
import bern.ui.Dialog;

/**
 * Stores and manages the application's tasks.
 */
public class TaskManager {
    private static TaskManager instance;
    private static final Dialog DIALOG = Dialog.getInstance();

    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Creates the initially empty manager used by the singleton instance.
     */
    private TaskManager() {
    }

    /**
     * Returns the singleton task manager instance.
     *
     * @return The singleton task manager instance.
     */
    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }

        return instance;
    }

    /**
     * Returns whether at least one task is stored.
     *
     * @return {@code true} if at least one task is stored; {@code false} otherwise.
     */
    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return The number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Removes all stored tasks.
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Returns a shallow copy of the stored task list.
     *
     * @return A shallow copy of the stored task list.
     */
    public List<Task> getTaskList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Loads tasks into the manager if it has not already been populated.
     *
     * @param taskList The tasks to load.
     * @return {@code true} if at least one task was loaded; {@code false} if the manager was already populated or
     *         the list was empty.
     */
    public boolean loadTaskList(List<Task> taskList) {
        if (hasTasks()) {
            // Reject operation if task list already has tasks in it
            // TODO: have better error messages
            return false;
        }

        tasks.addAll(taskList);

        return !tasks.isEmpty();
    }

    /**
     * Adds a task and returns the corresponding confirmation message.
     *
     * @param task The task to add.
     * @return The confirmation message for the added task.
     */
    public Response addTask(Task task) {
        tasks.add(task);

        return new TaskResponse(task, "*sizzle* Added a new task.");
    }

    /**
     * Returns a formatted list of the current tasks.
     *
     * @return A schedule response containing the current tasks, or a message indicating that no tasks are stored.
     */
    public Response listTasks() {
        if (tasks.isEmpty()) {
            return DIALOG.printNoTasksError();
        }

        return ScheduleResponse.getScheduleResponse("*sizzle* Here are your tasks: ", tasks).withTaskNumbers(tasks);
    }

    /**
     * Returns tasks whose names contain a case-insensitive match for a space-delimited search token.
     * Punctuation remains part of each token.
     *
     * @param searchToken The token to match against the space-delimited parts of each task name.
     * @return A schedule response containing matching tasks, or a message indicating no stored tasks or no matches.
     */
    public Response findTasks(String searchToken) {
        if (tasks.isEmpty()) {
            return DIALOG.printNoTasksError();
        }

        List<Task> filteredTasks = tasks.stream()
                .filter(task -> containsWord(task.getName(), searchToken))
                .toList();

        if (filteredTasks.isEmpty()) {
            return DIALOG.printMessage("*fsss...* No tasks match the given search token.");
        }

        return ScheduleResponse.getScheduleResponse("*FWOOSH* Here are matching tasks in your list:", filteredTasks)
                .withTaskNumbers(tasks);
    }

    /**
     * Returns whether a space-delimited part of the task name equals the search token, ignoring case.
     *
     * @param taskName The task name to split on spaces.
     * @param searchToken The token to match.
     * @return {@code true} if at least one part matches; {@code false} otherwise.
     */
    private static boolean containsWord(String taskName, String searchToken) {
        return Arrays.stream(taskName.split(" ")).anyMatch(word -> word.equalsIgnoreCase(searchToken));
    }

    /**
     * Returns the tasks associated with a calendar date, ordered by their reference date and time.
     * Todos are excluded because they have no associated date.
     *
     * @param dateTime The date to show; its optional time does not affect which tasks are included.
     * @return The schedule for that date, or a message indicating that no tasks are scheduled.
     */
    public Response showSchedule(DateTime dateTime) {
        ArrayList<Task> schedule = new ArrayList<>();

        for (Task task : tasks) {
            if (!task.isOnDay(dateTime)) {
                continue;
            }

            assert task instanceof IDateTimeComparable;

            schedule.add(task);
        }

        if (schedule.isEmpty()) {
            return DIALOG.printNoTasksError();
        }

        schedule.sort(Comparator.comparing(task -> ((IDateTimeComparable) task).getReferenceDateTime()));

        return ScheduleResponse.getScheduleResponse(
                "HOT! Here are the tasks for " + dateTime, schedule, dateTime.getDate()).withTaskNumbers(tasks);
    }

    /**
     * Marks a task as complete, or reports that it is already complete.
     *
     * @param taskNumber The one-based task number, between {@code 1} and {@link #size()} inclusive.
     * @return The task and a message describing its updated or existing completion state.
     */
    public Response markTask(int taskNumber) {
        assert 1 <= taskNumber && taskNumber <= tasks.size();
        Task task = tasks.get(taskNumber - 1);
        if (task.isDone()) {
            return new TaskResponse(task, "*Sizzle* The following task is already marked as done:");
        }

        task.setDone(true);

        return new TaskResponse(task, "*FWOOSH* I've marked this task as done:");
    }

    /**
     * Marks a task as incomplete, or reports that it is already incomplete.
     *
     * @param taskNumber The one-based task number, between {@code 1} and {@link #size()} inclusive.
     * @return The task and a message describing its updated or existing completion state.
     */
    public Response unmarkTask(int taskNumber) {
        assert 1 <= taskNumber && taskNumber <= tasks.size();
        Task task = tasks.get(taskNumber - 1);

        if (!task.isDone()) {
            return new TaskResponse(task, "*ss..* The following task is already marked as not done:");
        }

        task.setDone(false);

        return new TaskResponse(task, "*sss...* OK, I've marked this task as not done yet:");
    }

    /**
     * Deletes the task at the one-based index.
     *
     * @param taskNumber The one-based task number, between {@code 1} and {@link #size()} inclusive.
     * @return The deleted task and its confirmation message.
     */
    public Response deleteTask(int taskNumber) {
        assert 1 <= taskNumber && taskNumber <= tasks.size();
        Task task = tasks.remove(taskNumber - 1);
        return new TaskResponse(task, "*FWOOSH* OK, I've removed this task:");
    }
}
