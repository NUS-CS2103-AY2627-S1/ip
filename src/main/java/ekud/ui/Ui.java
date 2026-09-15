package ekud.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import ekud.task.Task;
import ekud.task.TaskDateTime;
import ekud.task.TaskList;

/**
 * Handles all interaction with the user.
 * The CLI variant reads typed input and prints messages. The GUI variant
 * records the same messages so {@link ekud.Ekud#getResponse} can return them.
 * Keeping I/O here keeps {@link ekud.Ekud} and the commands free of
 * {@code System.out} calls.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String NAME = "Ekud";
    // Credits: https://patorjk.com/software/taag/#p=testall&f=Alpha&t=EKUD&x=none&v=4&h=4&w=80&we=false
    private static final String BANNER = """
            ███████╗██╗  ██╗██╗   ██╗██████╗\s
            ██╔════╝██║ ██╔╝██║   ██║██╔══██╗
            █████╗  █████╔╝ ██║   ██║██║  ██║
            ██╔══╝  ██╔═██╗ ██║   ██║██║  ██║
            ███████╗██║  ██╗╚██████╔╝██████╔╝
            ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═════╝\s
            """;

    private final Scanner scanner;
    private final boolean isCli;
    private String lastMessage;

    /**
     * Creates a UI that reads from standard input and prints to standard output.
     */
    public Ui() {
        this(true);
    }

    /**
     * Creates a UI for either the terminal or the graphical window.
     *
     * @param isCli whether messages should be printed to the terminal
     */
    private Ui(boolean isCli) {
        this.isCli = isCli;
        this.scanner = isCli ? new Scanner(System.in) : null;
        this.lastMessage = "";
    }

    /**
     * Returns a UI that records messages for the GUI and does not use the terminal.
     *
     * @return a GUI-oriented UI
     */
    public static Ui forGui() {
        return new Ui(false);
    }

    /**
     * Returns the most recent message shown to the user, without CLI dividers.
     *
     * @return the last reply text
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * Prints the banner, welcome text, an optional startup joke, and a short
     * hint about the available commands. The GUI stores the same welcome text
     * without the ASCII banner.
     */
    public void showWelcome() {
        lastMessage = buildWelcomeBody();
        if (!isCli) {
            return;
        }
        printDivider();
        System.out.print(BANNER);
        System.out.println(lastMessage);
        printDivider();
    }

    /**
     * Prints a prompt so it is clear the chatbot is waiting for typed input.
     */
    public void showPrompt() {
        if (!isCli) {
            return;
        }
        System.out.print("> ");
        System.out.flush();
    }

    /**
     * Reads the next line of input.
     * If the input stream has ended (for example the user pressed Ctrl+Z),
     * {@code bye} is returned so the session can end cleanly.
     *
     * @return the line typed by the user, or {@code bye} on end of input
     */
    public String readCommand() {
        if (scanner == null || !scanner.hasNextLine()) {
            return "bye";
        }
        return scanner.nextLine();
    }

    /**
     * Confirms that a task was added and reports the new list size.
     *
     * @param task      the task that was just added
     * @param taskCount number of tasks after the add
     */
    public void showAdded(Task task, int taskCount) {
        display("Got it. I've added this task:\n  " + task + "\n"
                + "Now you have " + taskCount + " " + taskNoun(taskCount) + " in the list.");
    }

    /**
     * Prints the tasks numbered from 1, or a short message if the list is empty.
     *
     * @param tasks the list to display
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            display("Your task list is empty.");
            return;
        }
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        appendNumberedTasks(message, tasks);
        display(message.toString());
    }

    /**
     * Prints deadlines and events that occur on {@code date}, or a short
     * message if none match.
     *
     * @param date    the date that was queried
     * @param matches tasks that occur on that date, already filtered
     */
    public void showTasksOn(LocalDate date, List<Task> matches) {
        String formatted = TaskDateTime.toDisplayDate(date);
        if (matches.isEmpty()) {
            display("No deadlines or events on " + formatted + ".");
            return;
        }
        StringBuilder message = new StringBuilder("Here are the deadlines/events on ")
                .append(formatted)
                .append(':');
        appendNumberedTasks(message, matches);
        display(message.toString());
    }

    /**
     * Prints tasks whose descriptions contain {@code keyword}, or a short
     * message if none match.
     *
     * @param keyword the search text that was queried
     * @param matches tasks that contain the keyword, already filtered
     */
    public void showFound(String keyword, List<Task> matches) {
        if (matches.isEmpty()) {
            display("No tasks matching \"" + keyword + "\".");
            return;
        }
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        appendNumberedTasks(message, matches);
        display(message.toString());
    }

    /**
     * Confirms that a task was deleted and reports the new list size.
     *
     * @param task      the task that was just removed
     * @param taskCount number of tasks after the delete
     */
    public void showDeleted(Task task, int taskCount) {
        display("Noted. I've removed this task:\n  " + task + "\n"
                + "Now you have " + taskCount + " " + taskNoun(taskCount) + " in the list.");
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task that was just marked
     */
    public void showMarked(Task task) {
        display("Nice! I've marked this task as done:\n  " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task that was just unmarked
     */
    public void showUnmarked(Task task) {
        display("OK, I've marked this task as not done yet:\n  " + task);
    }

    /**
     * Prints the goodbye message.
     */
    public void showGoodbye() {
        display("Bye. Hope to see you again soon!");
    }

    /**
     * Prints a short guide to the supported commands and date formats.
     */
    public void showHelp() {
        display("""
                Here are the commands I understand:
                  todo DESCRIPTION
                  deadline DESCRIPTION /by DATE
                  event DESCRIPTION /from START /to END
                  list
                  on DATE
                  find KEYWORD
                  mark NUMBER
                  unmark NUMBER
                  delete NUMBER
                  help
                  bye

                Dates: 2019-12-02 or 2/12/2019, optionally with a time (1800 or 18:00).
                Type help anytime to see this guide again.""");
    }

    /**
     * Prints a user-facing error, such as invalid command arguments.
     *
     * @param message explanation of what went wrong
     */
    public void showError(String message) {
        display(message);
    }

    /**
     * Closes the input scanner. Call this when the command loop ends.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Records {@code message} as the latest reply. Prints it with dividers when
     * using the CLI.
     *
     * @param message text to show the user
     */
    private void display(String message) {
        lastMessage = message;
        if (isCli) {
            printDivider();
            System.out.println(message);
            printDivider();
        }
    }

    /**
     * Returns the welcome text without the ASCII banner or CLI dividers.
     *
     * @return greeting, optional joke, and command hint
     */
    private String buildWelcomeBody() {
        StringBuilder body = new StringBuilder("Hello! I'm ").append(NAME).append('.');
        String joke = DadJokeFetcher.fetch();
        if (joke != null) {
            body.append('\n').append(joke);
        }
        body.append("\n\n")
                .append("Add a task with todo, deadline, or event.\n")
                .append("Other commands: list, on <date>, find <keyword>, mark <number>, ")
                .append("unmark <number>, delete <number>, help, bye.");
        return body.toString();
    }

    /**
     * Appends {@code matches} as a 1-based numbered list, one task per line.
     *
     * @param message builder to append to
     * @param matches tasks to list
     */
    private static void appendNumberedTasks(StringBuilder message, Iterable<Task> matches) {
        int number = 1;
        for (Task task : matches) {
            message.append('\n').append(number).append(". ").append(task);
            number++;
        }
    }

    /**
     * Returns the singular or plural noun for a task count.
     *
     * @param taskCount number of tasks
     * @return {@code task} or {@code tasks}
     */
    private static String taskNoun(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }

    /**
     * Prints a horizontal divider to separate chatbot messages.
     */
    private void printDivider() {
        System.out.println(DIVIDER);
    }
}
