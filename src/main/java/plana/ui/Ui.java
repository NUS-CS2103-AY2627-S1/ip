package plana.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

import plana.client.Client;
import plana.client.ClientList;
import plana.task.Task;
import plana.task.TaskList;

/**
 * Handles Plana's console input and user-facing interface formatting.
 */
public class Ui implements AutoCloseable {
    private static final String BORDER_LINE = "____________________________________________________________";
    private static final String HELP_TEXT = """
            Hi hi! Need a hand? I'd be super happy to help you plan your day!
            Here's what I can do:

              Task commands:

              todo <description>
                  add a task

              deadline <description> /by <date>
                  add a deadline

              event <description> /from <start> /to <end>
                  add an event

              on <date>
                  show deadlines/events on a date

              find <keyword>
                  find tasks by description

              list
                  show all tasks

              delete <number>
                  delete a task

              mark <number>
                  mark a task as done

              unmark <number>
                  mark a task as not done

              Client commands:

              client add <name> /email <email>
                [/phone <phone>] [/address <address>]
                [/preferences <preferences>] [/notes <notes>]
                  add a client

              client list
                  show all clients

              client view <position>
                  show client details

              client find <keyword>
                  find clients by keyword

              client edit <position> /field <value>
                  edit a client

              client delete <position>
                  delete a client

              help or ?
                  show this help

              bye
                  say goodbye
            """.replace("\n", System.lineSeparator());

    private final Scanner scanner;
    private final PrintStream output;
    private final boolean shouldShowSeparators;

    /**
     * Creates a console user interface backed by standard input.
     */
    public Ui() {
        this(new Scanner(System.in), System.out, true);
    }

    /**
     * Creates a response-only interface that writes to the supplied stream.
     * This mode is used by the JavaFX interface to reuse Plana's existing
     * command response formatting without reading from standard input.
     *
     * @param output the stream that receives user-facing responses.
     */
    public Ui(PrintStream output) {
        this(new Scanner(InputStream.nullInputStream()), output, false);
    }

    private Ui(Scanner scanner, PrintStream output, boolean shouldShowSeparators) {
        this.scanner = scanner;
        this.output = Objects.requireNonNull(output);
        this.shouldShowSeparators = shouldShowSeparators;
    }

    /**
     * Displays Plana's startup banner and greeting.
     *
     * @param banner the text banner to display.
     * @param bannerArt the decorative banner art to display.
     */
    public void showWelcome(String banner, String bannerArt) {
        showLine();
        output.print(banner);
        output.print(bannerArt);
        output.println("Hi hi! I'm Plana, your cheerful task buddy!");
        output.println("Ready to make today a little more organised?");
        showLine();
    }

    /**
     * Checks whether another command is available from the user.
     *
     * @return true when another input line is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and echoes one command, including the separator printed after it.
     *
     * @return the command entered by the user
     */
    public String readCommand() {
        String command = scanner.nextLine();
        output.println(command);
        showLine();
        return command;
    }

    /**
     * Displays the available commands.
     */
    public void showHelp() {
        output.print(HELP_TEXT);
    }

    /**
     * Displays every task in its current order.
     *
     * @param tasks the tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        output.println(" Here are your tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            showTask(i, tasks.get(i));
        }
        showLine();
    }

    /**
     * Displays the heading for a keyword-filtered task query.
     */
    public void showMatchingTasksHeader() {
        output.println(" Here are the matching tasks in your list:");
    }

    /**
     * Displays the empty result message for a keyword-filtered task query.
     */
    public void showNoMatchingTasks() {
        output.println(" No matching tasks found.");
    }

    /**
     * Displays every client in creation order.
     *
     * @param clients the clients to display.
     */
    public void showClientList(ClientList clients) {
        output.println(" Here are your clients:");
        if (clients.size() == 0) {
            output.println(" No clients found.");
        } else {
            for (int i = 0; i < clients.size(); i++) {
                showClient(i, clients.get(i));
            }
        }
        showLine();
    }

    /**
     * Displays the heading for a client search.
     */
    public void showMatchingClientsHeader() {
        output.println(" Here are the matching clients in your list:");
    }

    /**
     * Displays the empty result message for a client search.
     */
    public void showNoMatchingClients() {
        output.println(" No matching clients found.");
    }

    /**
     * Displays a client using its current list-position reference.
     *
     * @param zeroBasedIndex the client's zero-based index.
     * @param client the client to display.
     */
    public void showClient(int zeroBasedIndex, Client client) {
        output.println(" C" + (zeroBasedIndex + 1) + ". " + client.getSummary());
    }

    /**
     * Displays the details of one client.
     *
     * @param reference the client's list-position reference.
     * @param client the client to display.
     */
    public void showClientDetails(String reference, Client client) {
        output.println(" Here are the details for client " + reference + ":");
        output.println(" Name: " + client.getName());
        output.println(" Email: " + client.getEmail());
        output.println(" Phone: " + displayOptional(client.getPhone()));
        output.println(" Address: " + displayOptional(client.getAddress()));
        output.println(" Preferences: " + displayOptional(client.getPreferences()));
        output.println(" Notes: " + displayOptional(client.getNotes()));
        showLine();
    }

    /**
     * Displays the confirmation for adding a client.
     *
     * @param client the newly added client.
     * @param clientCount the number of clients after adding.
     */
    public void showClientAdded(Client client, int clientCount) {
        output.println("Yay, I've added this client:");
        output.println("  [C" + clientCount + "] " + client.getSummary());
        output.println("Now you have " + clientCount + (clientCount == 1 ? " client" : " clients")
                + " in your list.");
        showLine();
    }

    /**
     * Displays the confirmation for editing a client.
     *
     * @param reference the client's list-position reference.
     * @param client the updated client.
     */
    public void showClientUpdated(String reference, Client client) {
        output.println("All set! I've updated this client:");
        output.println("  [" + reference + "] " + client.getSummary());
        showLine();
    }

    /**
     * Displays the confirmation for deleting a client.
     *
     * @param reference the deleted client's list-position reference.
     * @param client the deleted client.
     * @param remainingClientCount the number of clients after deletion.
     */
    public void showClientDeleted(String reference, Client client, int remainingClientCount) {
        output.println("All set! I've removed this client:");
        output.println("  [" + reference + "] " + client.getSummary());
        output.println("Now you have " + remainingClientCount
                + (remainingClientCount == 1 ? " client" : " clients") + " in the list.");
        showLine();
    }

    /**
     * Displays a task using the one-based number shown to the user.
     *
     * @param zeroBasedIndex the task's zero-based index.
     * @param task the task to display.
     */
    public void showTask(int zeroBasedIndex, Task task) {
        output.println(" " + (zeroBasedIndex + 1) + "." + task);
    }

    /**
     * Displays the heading for a date-filtered task query.
     *
     * @param displayDate the date formatted for display.
     */
    public void showTasksOnDateHeader(String displayDate) {
        output.println(" Here are the deadlines and events on " + displayDate + ":");
    }

    /**
     * Displays the empty result message for a date-filtered task query.
     *
     * @param displayDate the date formatted for display.
     */
    public void showNoTasksOnDate(String displayDate) {
        output.println(" No deadlines or events found on " + displayDate + ".");
    }

    /**
     * Displays the confirmation for adding a task.
     *
     * @param task the newly added task.
     * @param taskCount the number of tasks after adding.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Yay, I've added this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + (taskCount == 1 ? " task" : " tasks")
                + " in your list!");
        showLine();
    }

    /**
     * Displays the confirmation for deleting a task.
     *
     * @param task the deleted task.
     * @param remainingTaskCount the number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        output.println("All set! I've removed this task:");
        output.println("  " + task);
        output.println("Now you have " + remainingTaskCount
                + (remainingTaskCount == 1 ? " task" : " tasks") + " in the list.");
        showLine();
    }

    /**
     * Displays the confirmation for marking a task as done.
     *
     * @param task the completed task.
     */
    public void showTaskMarkedDone(Task task) {
        output.println("Amazing work! You've completed this task:");
        output.println("  " + task);
        showLine();
    }

    /**
     * Displays the confirmation for marking a task as not done.
     *
     * @param task the task marked as not done.
     */
    public void showTaskMarkedNotDone(Task task) {
        output.println("No worries at all! Plans can change, so I've marked this task as not done:");
        output.println("  " + task);
        showLine();
    }

    /**
     * Displays Plana's goodbye message.
     */
    public void showGoodbye() {
        output.println("Bye-bye! Keep being awesome, okay?");
        showLine();
    }

    /**
     * Displays an error message followed by the standard separator.
     *
     * @param message the user-facing error message.
     */
    public void showError(String message) {
        output.println(message);
        showLine();
    }

    /**
     * Displays the standard separator used between console interactions.
     */
    public void showLine() {
        if (shouldShowSeparators) {
            output.println(BORDER_LINE);
        }
    }

    private String displayOptional(String value) {
        return value.isBlank() ? "Not provided" : value;
    }

    /**
     * Closes the input scanner when the application exits.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
