package bobo.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import bobo.task.Task;
import bobo.task.TaskList;
import bobo.util.DateUtil;

/**
 * Handles all user interactions, input reading, and console/GUI outputs for Bobo.
 */
public class Ui {

    private static final String DIVIDER_LINE = "    ____________________________________________________________";

    private final Scanner scanner;
    private final StringBuilder responseBuffer = new StringBuilder();

    /**
     * Constructs a Ui object initializing standard input scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    private void output(String text) {
        responseBuffer.append(text).append("\n");
    }

    /**
     * Clears the current response buffer.
     */
    public void clearResponseBuffer() {
        responseBuffer.setLength(0);
    }

    /**
     * Returns the collected response buffer string and clears it.
     *
     * @return The formatted response string.
     */
    public String getResponseBuffer() {
        String result = responseBuffer.toString().trim();
        clearResponseBuffer();
        return result;
    }

    /**
     * Prints the welcome banner and initial greetings with personality based on task count.
     *
     * @param totalTasks Current total number of tasks.
     */
    public void showWelcome(int totalTasks) {
        output(PersonalityManager.getGreetingPhrase(totalTasks));
        output("What can I do for you?");
    }

    /**
     * Prints the welcome banner and initial greetings for an empty list.
     */
    public void showWelcome() {
        showWelcome(0);
    }


    /**
     * Displays a loading error message when file loading fails.
     */
    public void showLoadingError() {
        output("Error loading task data file. Starting with an empty task list.");
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message content.
     */
    public void showError(String message) {
        output(message);
    }

    /**
     * Displays a generic message to the user.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        output(message);
    }

    /**
     * Displays confirmation after adding a task.
     *
     * @param task       The added task.
     * @param totalTasks Current total number of tasks.
     */
    private void showTaskChangeResponse(String statusPhrase, Task task, int totalTasks) {
        output(statusPhrase);
        output("  " + task);
        output("Now you have " + totalTasks + " tasks in the list.");
    }

    /**
     * Displays confirmation after adding a task.
     *
     * @param task       The added task.
     * @param totalTasks Current total number of tasks.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        showTaskChangeResponse(PersonalityManager.getTaskAddedPhrase(totalTasks), task, totalTasks);
    }

    /**
     * Displays confirmation after deleting a task.
     *
     * @param task       The removed task.
     * @param totalTasks Remaining total number of tasks.
     */
    public void showTaskRemoved(Task task, int totalTasks) {
        showTaskChangeResponse(PersonalityManager.getTaskRemovedPhrase(totalTasks), task, totalTasks);
    }

    /**
     * Displays confirmation after marking or unmarking a task.
     *
     * @param task   The modified task.
     * @param isDone Status whether marked done or not done.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            output("Nice! I've marked this task as done:");
        } else {
            output("OK, I've marked this task as not done yet:");
        }
        output("  " + task);
    }

    /**
     * Displays all tasks in the given task list.
     *
     * @param taskList The TaskList instance to display.
     */
    public void showTaskList(TaskList taskList) {
        String emptyMsg = PersonalityManager.getEmptyListPhrase(taskList.size());
        printTaskList(taskList.getTasks(), "Here are the tasks in your list:", emptyMsg);
    }

    /**
     * Displays tasks occurring on a specified date.
     *
     * @param targetDate    The date queried.
     * @param matchingTasks List of matching tasks.
     */
    public void showTasksOnDate(LocalDate targetDate, List<Task> matchingTasks) {
        String formattedDate = DateUtil.formatForDisplay(targetDate);
        printTaskList(matchingTasks, "Here are the tasks occurring on " + formattedDate + ":",
                "No tasks found on " + formattedDate + ".");
    }

    /**
     * Displays tasks matching a search keyword.
     *
     * @param matchingTasks List of matching tasks to display.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        printTaskList(matchingTasks, "Here are the matching tasks in your list:",
                "No matching tasks found in your list.");
    }

    private void printTaskList(List<Task> tasks, String headerMessage, String emptyMessage) {
        if (tasks.isEmpty()) {
            output(emptyMessage);
            return;
        }
        output(headerMessage);
        IntStream.range(0, tasks.size())
                .forEach(i -> output((i + 1) + "." + tasks.get(i)));
    }

    /**
     * Displays farewell message when exiting Bobo.
     */
    public void showBye() {
        output("Bye. Hope to see you again soon!");
    }

    /**
     * Displays guidance on available commands, syntax formats, examples, and exit command.
     */
    public void showHelp() {
        output("Available commands and usage:");
        output("");
        output("1. list");
        output("   Format: list");
        output("   Example: list");
        output("");
        output("2. todo");
        output("   Format: todo <description>");
        output("   Example: todo read book");
        output("");
        output("3. deadline");
        output("   Format: deadline <description> /by <date>");
        output("   Example: deadline submit essay /by 2026-09-30");
        output("");
        output("4. event");
        output("   Format: event <description> /from <start> /to <end>");
        output("   Example: event meeting /from 2026-09-30 14:00 /to 2026-09-30 16:00");
        output("");
        output("5. mark");
        output("   Format: mark <task_number>");
        output("   Example: mark 2");
        output("");
        output("6. unmark");
        output("   Format: unmark <task_number>");
        output("   Example: unmark 2");
        output("");
        output("7. delete");
        output("   Format: delete <task_number>");
        output("   Example: delete 1");
        output("");
        output("8. find");
        output("   Format: find <keyword>");
        output("   Example: find book");
        output("");
        output("9. on");
        output("   Format: on <date>");
        output("   Example: on 2026-09-30");
        output("");
        output("10. help");
        output("    Format: help");
        output("    Example: help");
        output("");
        output("To exit the application:");
        output("   Type 'bye'");
    }
}
