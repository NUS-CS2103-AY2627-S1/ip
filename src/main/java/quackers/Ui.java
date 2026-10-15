package quackers;

import java.util.List;
import java.util.Scanner;

/**
 * Produces the user-facing messages of Quackers, and handles console input and output.
 *
 * <p>The message-producing methods return plain text rather than printing it, so that the same
 * wording can be reused by a console front end and by a GUI. The console front end indents each
 * line as it prints (see {@link #show(String)}); the GUI shows the text as-is.
 */
public class Ui implements AutoCloseable {
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = "+--------------------+\n"
            + "|      QUACKERS      |\n"
            + "+--------------------+";
    private static final String INDENT = "     ";

    private final Scanner scanner;

    /**
     * Creates a UI that reads console input from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns the application greeting.
     *
     * @return the greeting text.
     */
    public String getWelcomeMessage() {
        return "Hello! I'm Quackers.\nWhat can I do for you? Quack!";
    }

    /**
     * Returns the farewell message.
     *
     * @return the farewell text.
     */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Returns an error message.
     *
     * @param message the user-facing explanation.
     * @return the error text.
     */
    public String getErrorMessage(String message) {
        return message;
    }

    /**
     * Returns all tasks with one-based numbering.
     *
     * @param tasks the tasks to display.
     * @return the listing text.
     */
    public String getTasksMessage(List<Task> tasks) {
        return numberTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Returns the tasks that matched a find command, with one-based numbering.
     *
     * @param matchingTasks the tasks whose descriptions matched the keyword.
     * @return the listing text.
     */
    public String getMatchingTasksMessage(List<Task> matchingTasks) {
        return numberTasks("Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task the added task.
     * @param taskCount the resulting number of tasks.
     * @return the confirmation text.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns confirmation that a task was removed.
     *
     * @param task the removed task.
     * @param taskCount the resulting number of tasks.
     * @return the confirmation text.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return "Noted. I've removed this task:\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns confirmation that a task status changed.
     *
     * @param task the updated task.
     * @param isDone whether the task is now complete.
     * @return the confirmation text.
     */
    public String getTaskStatusChangedMessage(Task task, boolean isDone) {
        String heading = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return heading + "\n  " + task;
    }

    /**
     * Displays the console greeting, framed by separators and the banner.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        show(getWelcomeMessage());
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a message on the console, indenting every line of it.
     *
     * @param message the text to display.
     */
    public void show(String message) {
        for (String line : message.split("\n", -1)) {
            System.out.println(INDENT + line);
        }
    }

    /**
     * Displays the separator between command responses.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Returns whether another command is available.
     *
     * @return {@code true} when another line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the entered command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes the console input scanner.
     */
    @Override
    public void close() {
        scanner.close();
    }

    /**
     * Returns a heading followed by the given tasks, numbered from one.
     */
    private String numberTasks(String heading, List<Task> tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }
}
