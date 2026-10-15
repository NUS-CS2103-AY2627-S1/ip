package chocolate.ui;

import java.util.List;
import java.util.Scanner;

import chocolate.task.Task;
import chocolate.task.TaskList;

/**
 * Handles all console input and output for Chocolate.
 */
public class Ui {
    private static final String DIVIDER = "**************************************";
    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows the application greeting.
     */
    public void showWelcome() {
        showLine();
        System.out.println(getWelcomeMessage());
        System.out.println(DIVIDER);
    }

    /**
     * Returns the greeting shown when Chocolate starts.
     *
     * @return Greeting message without console dividers.
     */
    public String getWelcomeMessage() {
        return "Chocolate\nHello! I'm Chocolate, your task chocolatier.\nWhat shall we sweeten up today?";
    }

    /**
     * Reads the next complete user command.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows a divider between responses.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Shows the farewell message.
     */
    public void showGoodbye() {
        showResponse(getGoodbyeMessage());
    }

    /**
     * Returns Chocolate's farewell message.
     *
     * @return Farewell message without a console divider.
     */
    public String getGoodbyeMessage() {
        return "Thanks for visiting Chocolate's Cocoa Corner. See you soon!";
    }

    /**
     * Shows every task with a one-based display number.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        showResponse(getTaskListMessage(tasks));
    }

    /**
     * Returns the display text for every task.
     *
     * @param tasks Tasks to display.
     * @return Formatted task-list message without a console divider.
     */
    public String getTaskListMessage(TaskList tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        showResponse(getMatchingTasksMessage(tasks));
    }

    /**
     * Returns the display text for matching tasks.
     *
     * @param tasks Matching tasks to display.
     * @return Formatted matching-task message without a console divider.
     */
    public String getMatchingTasksMessage(List<Task> tasks) {
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Returns confirmation that active tasks were archived.
     *
     * @param taskCount Number of archived tasks.
     * @return Archive confirmation without a console divider.
     */
    public String getArchivedMessage(int taskCount) {
        return "Your " + taskCount + " task(s) are tucked safely into the archive.";
    }

    /**
     * Returns a message for an archive request with no active tasks.
     *
     * @return Empty-active-list message without a console divider.
     */
    public String getNoTasksToArchiveMessage() {
        return "The archive basket is empty.";
    }

    /**
     * Returns the display text for every archived task.
     *
     * @param archivedTasks Archived tasks to display.
     * @return Formatted archived-task message without a console divider.
     */
    public String getArchivedTaskListMessage(List<Task> archivedTasks) {
        if (archivedTasks.isEmpty()) {
            return "The archive basket is empty.";
        }

        StringBuilder message = new StringBuilder("Here are your archived treats:");
        for (int i = 0; i < archivedTasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(archivedTasks.get(i));
        }
        return message.toString();
    }

    /**
     * Shows confirmation that a task was completed.
     *
     * @param task Updated task.
     */
    public void showMarked(Task task) {
        showResponse(getMarkedMessage(task));
    }

    /**
     * Returns confirmation that a task was completed.
     *
     * @param task Updated task.
     * @return Completion confirmation without a console divider.
     */
    public String getMarkedMessage(Task task) {
        return "Delicious progress! I've marked this task as done:\n  [X] " + task.getDescription();
    }

    /**
     * Shows confirmation that a task was made incomplete.
     *
     * @param task Updated task.
     */
    public void showUnmarked(Task task) {
        showResponse(getUnmarkedMessage(task));
    }

    /**
     * Returns confirmation that a task was made incomplete.
     *
     * @param task Updated task.
     * @return Incomplete confirmation without a console divider.
     */
    public String getUnmarkedMessage(Task task) {
        return "No worries! I've marked this task as not done yet:\n  [ ] " + task.getDescription();
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showAdded(Task task, int taskCount) {
        showResponse(getAddedMessage(task, taskCount));
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     * @return Addition confirmation without a console divider.
     */
    public String getAddedMessage(Task task, int taskCount) {
        return "Sweet! I've added this to your list:\n  " + task
                + "\nYou now have " + taskCount + " tasks on your tray.";
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showDeleted(Task task, int taskCount) {
        showResponse(getDeletedMessage(task, taskCount));
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks after the deletion.
     * @return Deletion confirmation without a console divider.
     */
    public String getDeletedMessage(Task task, int taskCount) {
        return "Poof! This task has melted away:\n  " + task
                + "\nYou now have " + taskCount + " tasks left on your tray.";
    }

    /**
     * Shows a user-facing error message.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        showResponse(getErrorMessage(message));
    }

    /**
     * Returns a user-facing error message.
     *
     * @param message Explanation of the error.
     * @return Formatted error message without a console divider.
     */
    public String getErrorMessage(String message) {
        return "Oops! That crumbled. " + message;
    }

    /**
     * Shows a response followed by a divider in the console UI.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
        showLine();
    }

    /**
     * Shows a warning that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        System.out.println("Warning: Saved tasks could not be loaded. Starting with an empty list.");
    }
}
