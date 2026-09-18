package computah.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import computah.client.Client;
import computah.task.Task;

/**
 * Handles interactions with the user.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "  ____                            _        _     \n"
            + " / ___|___  _ __ ___  _ __  _   _| |_ __ _| |__  \n"
            + "| |   / _ \\| '_ ` _ \\| '_ \\| | | | __/ _` | '_ \\ \n"
            + "| |__| (_) | | | | | | |_) | |_| | || (_| | | | |\n"
            + " \\____\\___/|_| |_| |_| .__/ \\__,_|\\__\\__,_|_| |_|\n"
            + "                     |_|                          \n";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI component that reads from standard input.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI component that reads from the given input stream and writes to the given output stream.
     *
     * @param input input stream to read commands from.
     * @param output output stream to write messages to.
     */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Shows the greeting banner and initial prompt.
     */
    public void showWelcome() {
        output.println(LINE);
        output.println(BANNER);
        output.println("Hello! I'm Computah.");
        output.println("What can I do for you?");
        output.println(LINE);
    }

    /**
     * Returns whether another user command is available.
     *
     * @return true if standard input has another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return trimmed user command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Shows the divider line.
     */
    public void showLine() {
        output.println(LINE);
    }

    /**
     * Shows the farewell message.
     */
    public void showFarewell() {
        output.println("Bye. Hope to see you again soon!");
        output.println(LINE);
    }

    /**
     * Shows all tasks in the current task list.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        showTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Shows tasks that match a search keyword.
     *
     * @param tasks matching tasks to display.
     */
    public void showMatchingTasks(ArrayList<Task> tasks) {
        showTasks("Here are the matching tasks in your list:", tasks);
    }

    /**
     * Shows a numbered collection of tasks beneath the given heading.
     *
     * @param heading text displayed before the tasks.
     * @param tasks tasks to display.
     */
    private void showTasks(String heading, ArrayList<Task> tasks) {
        output.println(heading);
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
        output.println(LINE);
    }

    /**
     * Shows all clients in the current client list.
     *
     * @param clients clients to display.
     */
    public void showClientList(ArrayList<Client> clients) {
        output.println("Here are the clients in your list:");
        for (int i = 0; i < clients.size(); i++) {
            output.println((i + 1) + "." + clients.get(i));
        }
        output.println(LINE);
    }

    /**
     * Shows the confirmation message for an added client.
     *
     * @param client client that was added.
     * @param clientCount number of clients after adding the client.
     */
    public void showClientAdded(Client client, int clientCount) {
        output.println("Got it. I've added this client:");
        output.println("  " + client);
        output.println(formatClientCount(clientCount));
        output.println(LINE);
    }

    /**
     * Shows the confirmation message for an edited client.
     *
     * @param client client that was edited.
     */
    public void showClientEdited(Client client) {
        output.println("Got it. I've updated this client:");
        output.println("  " + client);
        output.println(LINE);
    }

    /**
     * Shows the confirmation message for a deleted client.
     *
     * @param client client that was deleted.
     * @param clientCount number of clients after deleting the client.
     */
    public void showClientDeleted(Client client, int clientCount) {
        output.println("Noted. I've removed this client:");
        output.println("  " + client);
        output.println(formatClientCount(clientCount));
        output.println(LINE);
    }

    /**
     * Formats the current number of clients with the correct singular or plural noun.
     *
     * @param clientCount current number of clients.
     * @return client-count message.
     */
    private String formatClientCount(int clientCount) {
        String noun = clientCount == 1 ? "client" : "clients";
        return "Now you have " + clientCount + " " + noun + " in the list.";
    }

    /**
     * Shows the confirmation message for an added task.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after adding the task.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Got it. I've added this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
        output.println(LINE);
    }

    /**
     * Shows the confirmation message for a deleted task.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after deleting the task.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
        output.println(LINE);
    }

    /**
     * Shows the confirmation message for a task marked as done.
     *
     * @param task task that was marked as done.
     */
    public void showTaskMarked(Task task) {
        output.println("Nice! I've marked this task as done:");
        output.println("  " + task);
        output.println(LINE);
    }

    /**
     * Shows the confirmation message for a task marked as not done.
     *
     * @param task task that was marked as not done.
     */
    public void showTaskUnmarked(Task task) {
        output.println("OK, I've marked this task as not done yet:");
        output.println("  " + task);
        output.println(LINE);
    }

    /**
     * Shows an error message.
     *
     * @param message error message without the standard Computah error prefix.
     */
    public void showError(String message) {
        output.println("OOPS!!! " + message);
        output.println(LINE);
    }
}
