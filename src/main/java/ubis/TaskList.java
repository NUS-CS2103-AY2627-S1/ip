package ubis;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages an in-memory list of tasks and supports operations such as adding, deleting, marking, and finding tasks.
 */
public class TaskList {
    private static final int INVALID_TASK_INDEX = -1;

    private List<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the task list and returns a confirmation message.
     *
     * @param task Task to be added.
     * @return Confirmation message of the added task.
     */
    public String addTask(Task task) {
        if (task == null) {
            return "";
        }
        tasks.add(task);
        return "added: " + task;
    }

    /**
     * Removes a task from the list using its 1-based number and returns a status message.
     *
     * @param taskNumber 1-based number of the task to remove.
     * @return Status message indicating success or failure.
     */
    public String removeTask(int taskNumber) {
        int index = getTaskIndex(taskNumber);
        if (index == INVALID_TASK_INDEX) {
            return getInvalidTaskNumberMessage(taskNumber);
        }

        String taskDescription = tasks.get(index).toString();
        tasks.remove(index);
        return "Okay, I've deleted " + taskDescription;
    }

    /**
     * Returns a formatted string listing all tasks currently stored.
     *
     * @return String representation of all tasks.
     */
    public String listTasks() {
        if (tasks.isEmpty()) {
            return "No tasks to show";
        }

        StringBuilder result = new StringBuilder();
        for (int index = 0; index < tasks.size(); index++) {
            appendTask(result, index);
        }
        return result.toString();
    }

    /**
     * Appends a task with its number in the full list, separating entries with newlines.
     */
    private void appendTask(StringBuilder result, int index) {
        if (!result.isEmpty()) {
            result.append("\n");
        }
        result.append(index + 1).append(": ").append(tasks.get(index));
    }

    /**
     * Marks a task as done using its 1-based number and returns a status message.
     *
     * @param taskNumber 1-based number of the task to mark.
     * @return Confirmation or error message.
     */
    public String markTask(int taskNumber) {
        int index = getTaskIndex(taskNumber);
        if (index == INVALID_TASK_INDEX) {
            return getInvalidTaskNumberMessage(taskNumber);
        }

        Task task = tasks.get(index);
        // Invariant: task retrieved from a valid index within bounds must not be null
        assert task != null : "Task to mark should not be null";

        task.mark();
        return "Nice! I've marked this task as DONE:\n  " + task;
    }

    /**
     * Marks a task as undone using its 1-based number and returns a status message.
     *
     * @param taskNumber 1-based number of the task to unmark.
     * @return Confirmation or error message.
     */
    public String unmarkTask(int taskNumber) {
        int index = getTaskIndex(taskNumber);
        if (index == INVALID_TASK_INDEX) {
            return getInvalidTaskNumberMessage(taskNumber);
        }

        Task task = tasks.get(index);
        // Invariant: task retrieved from a valid index within bounds must not be null
        assert task != null : "Task to unmark should not be null";

        task.unmark();
        return "Okay, I've marked this task NOT done yet:\n  " + task;
    }

    /**
     * Finds tasks containing the keyword and lists them with their full-list task numbers.
     *
     * @param keyword String keyword to search for in task names.
     * @return Formatted list of matching tasks.
     */
    public String find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return "Please provide a keyword to find.\nExample: find book";
        }

        String trimmedKeyword = keyword.trim();
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).getName().contains(trimmedKeyword)) {
                appendTask(result, index);
            }
        }

        return result.isEmpty() ? "No matching tasks found." : result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Task task : tasks) {
            result.append(task.stringify()).append("\n");
        }

        return result.toString();
    }

    /**
     * Returns whether the list contains the given one-based task number.
     */
    public boolean hasTaskNumber(int taskNumber) {
        return getTaskIndex(taskNumber) != INVALID_TASK_INDEX;
    }

    /**
     * Converts a one-based task number into a zero-based list index.
     *
     * @param taskNumber One-based task number.
     * @return Zero-based task index, or a sentinel value when the number is invalid.
     */
    private int getTaskIndex(int taskNumber) {
        assert tasks != null : "Tasks list should not be null";

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            return INVALID_TASK_INDEX;
        }
        return taskNumber - 1;
    }

    /**
     * Builds the shared response for an invalid task number.
     *
     * @param taskNumber Invalid one-based task number.
     * @return Error message for the invalid task number.
     */
    private String getInvalidTaskNumberMessage(int taskNumber) {
        return "There is no task number " + taskNumber + ". Enter a task number shown by \"list\".";
    }
}
