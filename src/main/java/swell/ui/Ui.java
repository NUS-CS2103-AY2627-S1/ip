package swell.ui;

import swell.task.Task;
import swell.task.TaskList;

/**
 * Handles user-facing text output.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String NEWLINE = System.lineSeparator();
    private static final String TASK_LINE_PREFIX = " - ";

    /**
     * Creates a user interface component for printing chatbot messages.
     */
    public Ui() {
    }

    /**
     * Prints the greeting message when the chatbot starts.
     */
    public void printGreeting() {
        printBlock(getGreeting(), false);
    }

    /**
     * Returns the greeting message shown when Swell starts.
     *
     * @return greeting message.
     */
    public String getGreeting() {
        return joinLines("Hi, I'm Swell, your calm task navigator.",
                "Send me a task, and we'll chart a steady course.");
    }

    /**
     * Prints one already-formatted Swell response.
     *
     * @param message message to print.
     */
    public void printMessage(String message) {
        printBlock(message, true);
    }

    /**
     * Prints the list of tasks or a message if the list is empty.
     *
     * @param tasks task list to print.
     */
    public void printTasks(TaskList tasks) {
        printBlock(getTasksText(tasks), true);
    }

    /**
     * Returns the text used to show all tasks.
     *
     * @param tasks task list to format.
     * @return formatted task list text.
     */
    public String getTasksText(TaskList tasks) {
        return getTaskListText(tasks, "Here's the current chart:",
                "Clear waters for now. Your task list is empty.");
    }

    /**
     * Prints the list of tasks matching a find command.
     *
     * @param tasks matching tasks to print.
     */
    public void printMatchingTasks(TaskList tasks) {
        printBlock(getMatchingTasksText(tasks), true);
    }

    /**
     * Returns the text used to show tasks matching a search.
     *
     * @param tasks matching tasks to format.
     * @return formatted matching task list text.
     */
    public String getMatchingTasksText(TaskList tasks) {
        return getTaskListText(tasks, "I found these on the current:",
                "No matching tasks surfaced this time.");
    }

    /**
     * Prints a message confirming that a task has been added to the list.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after adding the task.
     */
    public void printTaskAdded(Task task, int taskCount) {
        printBlock(getTaskAddedText(task, taskCount), true);
    }

    /**
     * Returns the text used after adding a task.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after adding the task.
     * @return task-added confirmation text.
     */
    public String getTaskAddedText(Task task, int taskCount) {
        return joinLines("Logged and anchored. I've added this task:",
                getTaskLine(task),
                getTaskCountText(taskCount));
    }

    /**
     * Prints a message confirming that a task has been marked as done.
     *
     * @param task task that was marked as done.
     */
    public void printTaskMarked(Task task) {
        printBlock(getTaskMarkedText(task), true);
    }

    /**
     * Returns the text used after marking a task as done.
     *
     * @param task task that was marked as done.
     * @return task-marked confirmation text.
     */
    public String getTaskMarkedText(Task task) {
        return joinLines("Smooth sailing. I've marked this task as done:",
                getTaskLine(task));
    }

    /**
     * Prints a message confirming that a task has been marked as not done.
     *
     * @param task task that was marked as not done.
     */
    public void printTaskUnmarked(Task task) {
        printBlock(getTaskUnmarkedText(task), true);
    }

    /**
     * Returns the text used after marking a task as not done.
     *
     * @param task task that was marked as not done.
     * @return task-unmarked confirmation text.
     */
    public String getTaskUnmarkedText(Task task) {
        return joinLines("Back on deck. I've marked this task as not done yet:",
                getTaskLine(task));
    }

    /**
     * Prints a message confirming that a task has been deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after deleting the task.
     */
    public void printTaskDeleted(Task task, int taskCount) {
        printBlock(getTaskDeletedText(task, taskCount), true);
    }

    /**
     * Returns the text used after deleting a task.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after deleting the task.
     * @return task-deleted confirmation text.
     */
    public String getTaskDeletedText(Task task, int taskCount) {
        return joinLines("Cleared from the chart. I've removed this task:",
                getTaskLine(task),
                getTaskCountText(taskCount));
    }

    /**
     * Prints an error message without stopping the chatbot.
     *
     * @param message error message to print.
     */
    public void printError(String message) {
        printBlock(getErrorText(message), true);
    }

    /**
     * Returns the text used to show an error.
     *
     * @param message error message to show.
     * @return formatted error text.
     */
    public String getErrorText(String message) {
        return "Oops! Choppy water ahead: " + message;
    }

    /**
     * Prints the goodbye message when the user exits the chatbot.
     */
    public void printGoodbye() {
        printBlock(getGoodbye(), true);
    }

    /**
     * Returns the goodbye message shown when Swell exits.
     *
     * @return goodbye message.
     */
    public String getGoodbye() {
        return "Docking for now. Come back when you're ready to set sail again.";
    }

    private String getTaskListText(TaskList tasks, String heading, String emptyMessage) {
        if (tasks.isEmpty()) {
            return emptyMessage;
        }

        StringBuilder result = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i += 1) {
            result.append(NEWLINE).append(i + 1).append(". ").append(tasks.get(i));
        }
        return result.toString();
    }

    private String getPluralSuffix(int taskCount) {
        return taskCount == 1 ? "" : "s";
    }

    private String getTaskLine(Task task) {
        return TASK_LINE_PREFIX + task;
    }

    private String getTaskCountText(int taskCount) {
        return "There " + getBeVerb(taskCount) + " now " + taskCount + " task"
                + getPluralSuffix(taskCount) + " on board.";
    }

    private String getBeVerb(int taskCount) {
        return taskCount == 1 ? "is" : "are";
    }

    private String joinLines(String... lines) {
        return String.join(NEWLINE, lines);
    }

    private void printBlock(String message, boolean shouldPrintLeadingBlankLine) {
        if (shouldPrintLeadingBlankLine) {
            System.out.println();
        }
        System.out.println(LINE);
        System.out.println(" " + message.replace(NEWLINE, NEWLINE + " "));
        System.out.println(LINE);
    }
}
