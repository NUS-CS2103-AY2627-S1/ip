package quu.ui;

import quu.task.Task;
import quu.task.TaskList;

/**
 * Builds every message the program shows the user.
 *
 * <p>Each method returns its message as a {@code String}, allowing the terminal and
 * JavaFX front ends to use the same wording.
 */
public class Ui {
    private static final String BANNER =
              "  ___\n"
            + " / _ \\ _   _ _   _\n"
            + "| | | | | | | | | |\n"
            + "| |_| | |_| | |_| |\n"
            + " \\__\\_\\\\__,_|\\__,_|\n";

    /**
     * Returns the program's ASCII-art logo.
     *
     * @return the logo
     */
    public String getBanner() {
        return BANNER;
    }

    /**
     * Returns the opening greeting.
     *
     * @param name the name the chatbot introduces itself by
     * @return the greeting
     */
    public String getGreeting(String name) {
        return String.format("Hello! I'm %s.%nWhat can I do for you?", name);
    }

    /**
     * Returns the confirmation that a task was added.
     *
     * @param task the task that was added
     * @param size the number of tasks now in the list
     * @return the confirmation message
     */
    public String getAdded(Task task, int size) {
        return String.format("Got it. I've added this task:%n  %s%nNow you have %s in the list.",
                task, describeCount(size));
    }

    /**
     * Returns the confirmation that a task was removed.
     *
     * @param task the task that was removed
     * @param size the number of tasks left in the list
     * @return the confirmation message
     */
    public String getRemoved(Task task, int size) {
        return String.format("Noted. I've removed this task:%n %s%nNow you have %s in the list.",
                task, describeCount(size));
    }

    /**
     * Returns the confirmation that a task's details were changed.
     *
     * @param task the task as it now stands, after the edits
     * @return the confirmation message
     */
    public String getEdited(Task task) {
        return String.format("Got it. I've updated this task:%n %s", task);
    }

    /**
     * Returns the confirmation that a task was marked as done.
     *
     * @param task the task that was marked
     * @return the confirmation message
     */
    public String getMarked(Task task) {
        return String.format("Nice! I've marked this task as done:%n %s", task);
    }

    /**
     * Returns the confirmation that a task was marked as not done.
     *
     * @param task the task that was unmarked
     * @return the confirmation message
     */
    public String getUnmarked(Task task) {
        return String.format("OK, I've marked this task as not done yet:%n %s", task);
    }

    /**
     * Returns every task in the list, numbered from one.
     *
     * @param taskList the tasks to render
     * @return the heading followed by the numbered tasks
     */
    public String getList(TaskList taskList) {
        if (taskList.getSize() == 0) {
            return "There are no tasks in your list yet. Add one with todo, deadline or event.";
        }
        return "Here are the tasks in your list:" + buildNumberedList(taskList);
    }

    /**
     * Returns the tasks that matched a search, numbered from one.
     *
     * @param taskList the matching tasks
     * @return the heading followed by the numbered tasks
     */
    public String getFound(TaskList taskList) {
        if (taskList.getSize() == 0) {
            return "No tasks match that keyword.";
        }
        return "Here are the matching tasks in your list:" + buildNumberedList(taskList);
    }

    /**
     * Returns the prompt shown when the user sends nothing at all.
     *
     * @return the prompt
     */
    public String getEmptyInput() {
        return "Please type a command. Type list to see your tasks.";
    }

    /**
     * Returns the message carried by an exception.
     *
     * @param exception the exception to report
     * @return the exception's message
     */
    public String getException(Exception exception) {
        return exception.getMessage();
    }

    /**
     * Returns a report that tasks could not be saved to disk.
     *
     * @param message the detail to show the user
     * @return the report
     */
    public String getSaveError(String message) {
        return message;
    }

    /**
     * Returns a report that tasks could not be loaded from disk.
     *
     * @param message the detail to show the user
     * @return the report
     */
    public String getLoadingError(String message) {
        return message;
    }

    /**
     * Returns the farewell shown when the user exits.
     *
     * @return the farewell
     */
    public String getGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Returns a count of tasks with the noun agreeing with it, such as {@code 1 task} or
     * {@code 2 tasks}.
     *
     * @param size the number of tasks to describe
     * @return the count and its noun
     */
    private String describeCount(int size) {
        return String.format("%d task%s", size, size == 1 ? "" : "s");
    }

    /**
     * Builds the numbered lines for a task list, each on its own new line.
     *
     * @param taskList the tasks to render
     * @return the numbered lines, each prefixed by a line separator
     */
    private String buildNumberedList(TaskList taskList) {
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < taskList.getSize(); i++) {
            lines.append(String.format("%n%d.%s", i + 1, taskList.getTaskAt(i)));
        }
        return lines.toString();
    }
}
