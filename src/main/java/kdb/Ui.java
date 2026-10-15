package kdb;

import java.util.Scanner;

/**
 * Handles interaction with the user through the console.
 */
public class Ui implements AutoCloseable {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner;

    /** Creates a console user interface. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays the startup greeting and command guide. */
    public void showWelcome() {
        System.out.println(welcomeBanner());
        System.out.println(welcomeMessage());
    }

    /** Returns the KDB banner for display. */
    public static String welcomeBanner() {
        return "mm   mm   mmmmmm    mmmmmmm\n"
                + "##  ##    ##    ##  ##    ##\n"
                + "##m##     ##    ##  ##    ##\n"
                + "#####     ##    ##  #######\n"
                + "##  ##m   ##    ##  ##    ##\n"
                + "##   ##m  ##mmm##   ##mmmm##";
    }

    /** Returns the startup greeting and command guide for display. */
    public static String welcomeMessage() {
        return "Hello, I am KDB and I will be your personal assistant!\n"
                + "I can do the following:\n\n"
                + commandGuide();
    }

    /** Reads the next command from the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the standard console divider. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Displays the farewell message. */
    public void showBye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /** Displays all tasks with their one-based list numbers. */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here's the current match sheet:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays tasks matching a search keyword. */
    public void showMatchingTasks(TaskList tasks) {
        System.out.println("Here are the matching plays:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays confirmation after adding a task. */
    public void showAdded(Task task, int taskCount) {
        System.out.println("Perfect pass! I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays confirmation after marking a task done. */
    public void showMarked(Task task) {
        System.out.println("Great finish! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /** Displays confirmation after marking a task incomplete. */
    public void showUnmarked(Task task) {
        System.out.println("Back in play! I've marked this task as not done:");
        System.out.println("  " + task);
    }

    /** Displays confirmation after deleting a task. */
    public void showDeleted(Task task, int taskCount) {
        System.out.println("Cleared from the pitch! I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays an error message. */
    public void showError(String message) {
        System.out.println(message);
    }

    /** Displays help for an unrecognized command. */
    public void showUnknownCommandHelp() {
        showError(unknownCommandHelp());
    }

    /** Returns help text for an unrecognized command. */
    public static String unknownCommandHelp() {
        return "That pass went astray. Here's what I can do:\n\n"
                + commandGuide();
    }

    /** Returns the available commands and their purposes. */
    private static String commandGuide() {
        return String.join("\n",
                "todo <description> - Add a task without a deadline.",
                "deadline <description> /by <date/time> - Add a task with a deadline.",
                "event <description> /from <start> /to <end> - Add an event.",
                "list - Show all tasks.",
                "find <keyword> - Find tasks containing a keyword.",
                "mark <task number> - Mark a task as done.",
                "unmark <task number> - Mark a task as not done.",
                "delete <task number> - Delete a task.",
                "bye - Exit KDB.");
    }

    /** Closes the console input scanner. */
    @Override
    public void close() {
        scanner.close();
    }
}
