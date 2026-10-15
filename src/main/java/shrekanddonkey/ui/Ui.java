package shrekanddonkey.ui;

import java.util.List;
import java.util.Scanner;

import shrekanddonkey.task.Task;

/**
 * Handles console input and output for the chatbot.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " ____  _              _        _              _ ____              _\n"
            + "/ ___|| |__  _ __ ___| | __   / \\   _ __   __| |  _ \\  ___  _ __ | | _____ _   _\n"
            + "\\___ \\| '_ \\| '__/ _ \\ |/ /  / _ \\ | '_ \\ / _` | | | |/ _ \\| '_ \\| |/ / _ \\ | | |\n"
            + " ___) | | | | | |  __/   <  / ___ \\| | | | (_| | |_| | (_) | | | |   <  __/ |_| |\n"
            + "|____/|_| |_|_|  \\___|_|\\_\\/_/   \\_\\_| |_|\\__,_|____/ \\___/|_| |_|_|\\_\\___|\\__, |\n"
            + "                                                                            |___/\n";

    private final Scanner scanner;
    private final StringBuilder outputBuffer = new StringBuilder();
    private boolean isError = false;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Clears recorded messages in the output buffer and resets error status.
     */
    public void clearOutput() {
        outputBuffer.setLength(0);
        isError = false;
    }

    /**
     * Returns whether the last recorded response was an error.
     *
     * @return true if an error was recorded, false otherwise.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Returns all messages recorded in the output buffer since the last clear.
     *
     * @return the recorded message string.
     */
    public String getRecordedOutput() {
        return outputBuffer.toString().trim();
    }

    /**
     * Reads one command from the user.
     *
     * @return the command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the chatbot welcome message.
     */
    public void showWelcome() {
        showDivider();
        System.out.print(BANNER);
        showMessage("Grrr GET OUT OF MY SWAMP! Ohh, I didn't see you there stranger! I'm ShrekAndDonkey.");
        showMessage("What can I do for you?");
        showDivider();
    }

    /**
     * Displays the chatbot goodbye message.
     */
    public void showGoodbye() {
        showDivider();
        showMessage("FEE FIE FOE FUMP. GET OUT OF MY SWAMP!");
        showDivider();
    }

    /**
     * Displays a visual divider between chatbot responses.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays one chatbot message.
     *
     * @param message message to display.
     */
    public void showMessage(String message) {
        assert message != null : "Message to display cannot be null";
        System.out.println(message);
        outputBuffer.append(message).append("\n");
    }

    /**
     * Displays one error message and marks the current response as an error.
     *
     * @param message error message to display.
     */
    public void showError(String message) {
        assert message != null : "Error message to display cannot be null";
        isError = true;
        showMessage(message);
    }

    /**
     * Displays the confirmation after a task is added.
     *
     * @param task task that was added.
     * @param taskCount number of tasks in the list.
     */
    public void showTaskAdded(Task task, int taskCount) {
        assert task != null : "Task cannot be null";
        assert taskCount >= 0 : "Task count cannot be negative";
        showMessage(" Alright, added to the swamp list:");
        showMessage("   " + task);
        showMessage(" Now ye have " + taskCount + " tasks in the swamp.");
    }

    /**
     * Displays all tasks with one-based numbering.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        assert tasks != null : "Task list cannot be null";
        showIndexedTasks(" Here's what's lurkin' in the swamp:", tasks);
    }

    /**
     * Displays all matching tasks with one-based numbering.
     *
     * @param tasks matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        assert tasks != null : "Matching tasks list cannot be null";
        showIndexedTasks(" Donkey found these in the swamp:", tasks);
    }

    private void showIndexedTasks(String header, List<Task> tasks) {
        assert header != null : "Header cannot be null";
        assert tasks != null : "Task list cannot be null";
        showMessage(header);
        for (int i = 0; i < tasks.size(); i++) {
            showMessage(" " + (i + 1) + "." + tasks.get(i));
        }
    }
}
