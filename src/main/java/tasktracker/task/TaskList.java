package tasktracker.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import tasktracker.exception.TaskTrackerException;
import tasktracker.ui.Message;

/**
 * Represents a collection of tasks and handles task-level operations such as adding,
 * formatting output, updating task completion status, and restoring deleted tasks.
 */
public class TaskList {
    // Set a limit to 100 to prevent user misuse.
    private static final int MAX_TASKS = 100;
    private static final String NEWLINE = "\n";
    private static final String ITEM_SEPARATOR = ". ";

    private final List<Task> taskList;
    private Task lastDeletedTask = null;
    private int lastDeletedIndex = -1;

    /**
     * Initializes an empty {@code TaskList}.
     */
    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    /**
     * Initializes a {@code TaskList} with preloaded tasks (from {@code Storage}).
     *
     * @param savedTasks List of tasks loaded from disk.
     */
    public TaskList(List<Task> savedTasks) {
        assert savedTasks != null : "Initial saved tasklist should not be null.";
        this.taskList = new ArrayList<>(savedTasks);
    }

    /**
     * Initializes a {@code TaskList} with an arbitrary number of initial tasks.
     *
     * @param tasks Initial tasks to populate the list with.
     */
    public TaskList(Task... tasks) {
        assert tasks != null : "Initial tasks array should not be null.";
        this.taskList = new ArrayList<>(List.of(tasks));
    }

    /**
     * Returns the underlying list of tasks for saving.
     *
     * @return Unmodifiable list of current tasks.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(taskList);
    }

    /**
     * Adds a task to the list if space permits.
     *
     * @param task Task to be added.
     * @return Message confirming addition of the task.
     * @throws TaskTrackerException If the task list has reached {@code MAX_TASKS} capacity.
     */
    public String add(Task task) throws TaskTrackerException {
        assert task != null : "Task to add should not be null.";
        if (taskList.size() >= MAX_TASKS) {
            throw new TaskTrackerException(Message.ERR_TASK_LIST_FULL);
        }

        int initialSize = taskList.size();
        taskList.add(task);
        assert taskList.size() == initialSize + 1 : "Tasklist size should increase by 1 per add.";
        return Message.MSG_TASK_ADDED + " " + task + NEWLINE
                + Message.getMsgTaskCount(taskList.size());
    }

    /**
     * Deletes a task from the list by its 1-based index and saves it for undo.
     *
     * @param index 1-based index of the task to remove.
     * @return Confirmation message of the deleted task.
     * @throws TaskTrackerException If the task index is out of bounds.
     */
    public String deleteTask(int index) throws TaskTrackerException {
        validateIndex(index);

        int initialSize = taskList.size();
        int zeroBasedIndex = toZeroBasedIndex(index);
        Task removedTask = taskList.remove(zeroBasedIndex);
        assert removedTask != null : "Removed task should not be null.";
        assert taskList.size() == initialSize - 1 : "Tasklist size should decrease by 1 per delete.";

        this.lastDeletedTask = removedTask;
        this.lastDeletedIndex = zeroBasedIndex;

        return Message.MSG_TASK_REMOVED + " " + removedTask + NEWLINE
                + Message.getMsgTaskCount(taskList.size()) + NEWLINE + NEWLINE
                + Message.TIP_UNDO;
    }

    /**
     * Checks whether there is a recently deleted task available to restore.
     *
     * @return {@code true} if a deleted task can be restored, {@code false} otherwise.
     */
    public boolean hasDeletedTaskToUndo() {
        return lastDeletedTask != null;
    }

    /**
     * Clears any remembered deletion state, invalidating future undo attempts.
     */
    public void clearUndoHistory() {
        this.lastDeletedTask = null;
        this.lastDeletedIndex = -1;
    }

    /**
     * Restores the most recently deleted task back to its original position
     * and clears the undo cache.
     *
     * @return Confirmation message of the restored task.
     * @throws TaskTrackerException If no deleted task exists to undo or the list is full.
     */
    public String undoDelete() throws TaskTrackerException {
        if (!hasDeletedTaskToUndo()) {
            throw new TaskTrackerException(Message.ERR_NO_UNDO_TASK);
        }
        if (taskList.size() >= MAX_TASKS) {
            throw new TaskTrackerException(Message.ERR_TASK_LIST_FULL);
        }

        assert lastDeletedTask != null : "Task to restore should not be null.";
        int restoreIndex = Math.min(lastDeletedIndex, taskList.size());
        int initialSize = taskList.size();

        taskList.add(restoreIndex, lastDeletedTask);
        assert taskList.size() == initialSize + 1 : "Tasklist size should increase by 1 per undo restoration.";

        Task restoredTask = lastDeletedTask;
        clearUndoHistory();

        return Message.MSG_TASK_RESTORED + NEWLINE
                + "  " + restoredTask + NEWLINE
                + Message.getMsgTaskCount(taskList.size());
    }

    /**
     * Finds and formats all tasks containing the specified keyword in their description.
     *
     * @param keyword The substring keyword to search for.
     * @return Formatted string containing all matching tasks, or a message indicating no matches.
     */
    public String findTasks(String keyword) {
        assert keyword != null : "Search keyword should not be null.";
        List<Task> matchingTasks = filterTasksByKeyword(keyword);

        if (matchingTasks.isEmpty()) {
            return Message.ERR_NO_MATCHING_TASKS + keyword + NEWLINE;
        }

        return Message.MSG_FIND_MATCHING + formatNumberedList(matchingTasks);
    }

    /**
     * Generates a formatted string representing all tasks currently stored in the list.
     *
     * @return Formatted string of all tasks with 1-based indexing, else tells user list is empty.
     */
    public String getFormattedList() {
        if (taskList.isEmpty()) {
            return Message.ERR_TASK_LIST_EMPTY;
        }

        return IntStream.range(0, taskList.size())
                .mapToObj(i -> (i + 1) + ". " + taskList.get(i))
                .collect(Collectors.joining(NEWLINE));
    }

    /**
     * Sets the status of a task identified by its 1-based index.
     *
     * @param index  1-based task index.
     * @param isDone {@code true} to mark as done, {@code false} to mark as undone.
     * @return Confirmation message of the updated task status.
     * @throws TaskTrackerException If the task index is out of bounds.
     */
    public String setTaskStatus(int index, boolean isDone) throws TaskTrackerException {
        validateIndex(index);

        Task taskToUpdate = taskList.get(index - 1);
        assert taskToUpdate != null : "Retrieved task to update should not be null.";

        if (isDone) {
            taskToUpdate.markAsDone();
            return Message.MSG_TASK_MARKED + " " + taskToUpdate;
        } else {
            taskToUpdate.markAsUndone();
            return Message.MSG_TASK_UNMARKED + " " + taskToUpdate;
        }
    }

    /**
     * Validates that the provided 1-based index falls within the active bounds of the list.
     *
     * @param index 1-based index to check.
     * @throws TaskTrackerException If index is less than 1 or exceeds current list size.
     */
    private void validateIndex(int index) throws TaskTrackerException {
        if (index < 1 || index > taskList.size()) {
            throw new TaskTrackerException(Message.getErrOutOfBounds(taskList.size()));
        }
    }

    /**
     * Converts a 1-based index into an internal 0-based collection index.
     *
     * @param oneBasedIndex The 1-based index supplied by user input.
     * @return The corresponding 0-based index.
     */
    private int toZeroBasedIndex(int oneBasedIndex) {
        return oneBasedIndex - 1;
    }

    /**
     * Filters tasks whose descriptions match the provided keyword (case-insensitive).
     * @param keyword Substring to search within task descriptions.
     * @return List of matching tasks.
     */
    private List<Task> filterTasksByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return taskList.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Formats a list of tasks into a numbered string representation.
     *
     * @param tasks List of tasks to format.
     * @return Formatted numbered list string.
     */
    private String formatNumberedList(List<Task> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ITEM_SEPARATOR + tasks.get(i))
                .collect(Collectors.joining(NEWLINE));
    }
}
