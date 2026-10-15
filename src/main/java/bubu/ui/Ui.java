package bubu.ui;

import java.util.List;

import bubu.task.Task;

/**
 * Formats chatbot responses for display in the graphical user interface.
 */
public class Ui {
    // Welcome and farewell messages.
    private static final String WELCOME_BANNER = "/\\___/\\ \n"
            + "(  >.<  )  Hello! I'm BUBU!\n";
    private static final String WELCOME_MESSAGE = "What can I do for you? Meow!";
    private static final String GOODBYE_MESSAGE =
            "Bye! This window will close in 3 seconds.\n"
                    + "Hope to see you again soon! Meow!";

    // Empty-state and task-operation messages.
    private static final String EMPTY_LIST_MESSAGE = "Meow! Your task list is empty.";
    private static final String NO_MATCHES_MESSAGE = "Meow! No matching tasks found.";
    private static final String NO_UPCOMING_TASKS_MESSAGE =
            "Meow! No incomplete tasks are due within the next 3 days.";
    private static final String TASK_ADDED_MESSAGE = "Got it meow. I've added this task:";
    private static final String TASK_DELETED_MESSAGE = "Meow! I've removed this task:";
    private static final String TASK_DONE_MESSAGE = "Meow! I've marked this task as done:";
    private static final String TASK_NOT_DONE_MESSAGE = "Meow! I've marked this task as not done yet:";

    // Formats and task-count wording.
    private static final String TASK_ITEM_FORMAT = "  %s";
    private static final String NUMBERED_TASK_FORMAT = "%d. %s";
    private static final String TASK_COUNT_MESSAGE = "Now you have %d %s in the list. Meow!";
    private static final String SINGULAR_TASK_NOUN = "task";
    private static final String PLURAL_TASK_NOUN = "tasks";

    // Headers used when displaying task collections.
    private static final String TASK_LIST_SINGULAR_HEADER = "Meow! Here is the task in your list:";
    private static final String TASK_LIST_PLURAL_HEADER = "Meow! Here are the tasks in your list:";
    private static final String MATCHING_TASK_SINGULAR_HEADER =
            "Meow! Here is the matching task in your list:";
    private static final String MATCHING_TASK_PLURAL_HEADER =
            "Meow! Here are the matching tasks in your list:";
    private static final String UPCOMING_TASK_SINGULAR_HEADER =
            "Meow! Here is an incomplete task due within the next 3 days:";
    private static final String UPCOMING_TASK_PLURAL_HEADER =
            "Meow! Here are the incomplete tasks due within the next 3 days:";
    private static final int SINGULAR_TASK_COUNT = 1;

    /** Stores the response generated for the most recent user command. */
    private final StringBuilder response = new StringBuilder();

    /**
     * Appends the chatbot greeting to the current response.
     */
    public void showWelcome() {
        appendLine(WELCOME_BANNER.trim());
        appendLine(WELCOME_MESSAGE);
    }

    /**
     * Appends the farewell message to the current response.
     */
    public void showGoodbye() {
        appendLine(GOODBYE_MESSAGE);
    }

    /**
     * Appends an error message from command processing to the current response.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        appendLine(message);
    }

    /**
     * Appends all tasks in their numbered list form to the current response.
     *
     * @param tasks List of tasks to show.
     */
    public void showTaskList(List<Task> tasks) {
        showTasksWithHeader(tasks, EMPTY_LIST_MESSAGE,
                TASK_LIST_SINGULAR_HEADER, TASK_LIST_PLURAL_HEADER);
    }

    /**
     * Appends all matching tasks that match the search keyword to the current response.
     *
     * @param tasks List of matching tasks.
     */
    public void showMatchingTasks(List<Task> tasks) {
        showTasksWithHeader(tasks, NO_MATCHES_MESSAGE,
                MATCHING_TASK_SINGULAR_HEADER, MATCHING_TASK_PLURAL_HEADER);
    }

    /**
     * Appends incomplete tasks that are due within the reminder window.
     *
     * @param tasks Upcoming tasks to show.
     */
    public void showUpcomingTasks(List<Task> tasks) {
        showTasksWithHeader(tasks, NO_UPCOMING_TASKS_MESSAGE,
                UPCOMING_TASK_SINGULAR_HEADER, UPCOMING_TASK_PLURAL_HEADER);
    }

    /**
     * Appends confirmation that a task was added to the current response.
     *
     * @param task Added task.
     * @param taskCount Total number of tasks remaining.
     */
    public void showTaskAdded(Task task, int taskCount) {
        appendLine(TASK_ADDED_MESSAGE);
        appendLine(String.format(TASK_ITEM_FORMAT, task));
        showTaskCount(taskCount);
    }

    /**
     * Appends confirmation that a task's completion status changed to the current response.
     *
     * @param task Target task.
     * @param isDone True if task is marked complete, false otherwise.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        appendLine(isDone ? TASK_DONE_MESSAGE : TASK_NOT_DONE_MESSAGE);
        appendLine(String.format(TASK_ITEM_FORMAT, task));
    }

    /**
     * Appends confirmation that a task was deleted to the current response.
     *
     * @param task Removed task.
     * @param taskCount Total number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        appendLine(TASK_DELETED_MESSAGE);
        appendLine(String.format(TASK_ITEM_FORMAT, task));
        showTaskCount(taskCount);
    }

    /**
     * Renders either an empty-state message or an enumerated list of tasks with the given headers.
     */
    private void showTasksWithHeader(List<Task> tasks, String emptyMessage,
                                     String singularHeader, String pluralHeader) {
        if (tasks.isEmpty()) {
            appendLine(emptyMessage);
            return;
        }

        String header = tasks.size() == SINGULAR_TASK_COUNT ? singularHeader : pluralHeader;
        appendLine(header);
        for (int index = 0; index < tasks.size(); index++) {
            appendLine(String.format(NUMBERED_TASK_FORMAT, index + 1, tasks.get(index)));
        }
    }

    /**
     * Appends the singular or plural task-count message to the current response.
     */
    private void showTaskCount(int taskCount) {
        String noun = taskCount == SINGULAR_TASK_COUNT ? SINGULAR_TASK_NOUN : PLURAL_TASK_NOUN;
        appendLine(String.format(TASK_COUNT_MESSAGE, taskCount, noun));
    }

    /**
     * Appends a text line followed by a platform-independent line separator.
     */
    private void appendLine(String line) {
        response.append(line).append(System.lineSeparator());
    }

    /**
     * Clears the response buffer to prepare for the next command.
     */
    public void clearResponse() {
        response.setLength(0);
    }

    /**
     * Returns the current response as a string.
     *
     * @return Current response.
     */
    public String getResponse() {
        return response.toString();
    }
}
