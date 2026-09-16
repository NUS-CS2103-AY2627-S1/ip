package aider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import aider.model.Task;
import aider.model.TaskList;
import aider.util.DateTimeParser;

/** Handles all console input and output for Aider. */
public class Ui implements AutoCloseable {
    /** Separates the application banner and individual command responses. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** ASCII art displayed when the application starts. */
    private static final String BANNER = "    _    ___ ____  _____ ____ \n"
            + "   / \\  |_ _|  _ \\| ____|  _ \\ \n"
            + "  / _ \\  | || | | |  _| | |_) |\n"
            + " / ___ \\ | || |_| | |___|  _ < \n"
            + "/_/   \\_\\___|____/|_____|_| \\";
    /** Reads commands from standard input. */
    private final Scanner scanner;

    /** Creates a user interface reading from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows the startup greeting. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println(Personality.welcomeMessage());
        System.out.println(SEPARATOR);
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next command, then separates the response. */
    public String readCommand() {
        String command = scanner.nextLine().trim();
        System.out.println(SEPARATOR);
        return command;
    }

    /** Shows the goodbye response. */
    public void showGoodbye() {
        System.out.println(Personality.goodbyeMessage());
        System.out.println(SEPARATOR);
    }

    /** Shows a loading failure. */
    public void showLoadingError(String message) {
        System.out.println("Could not load tasks from storage: " + message);
    }

    /** Shows a command error. */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    /** Shows a save failure. */
    public void showSavingError(String message) {
        System.out.println("Could not save tasks to storage: " + message);
    }

    /** Displays all tasks. */
    public void showTasks(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        if (tasks.isEmpty()) {
            System.out.println("  (no tasks yet)");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
        }
    }

    /** Displays tasks matching a search keyword. */
    public void showFound(ArrayList<Task> tasks) {
        System.out.println("Here are the matching tasks:");
        if (tasks.isEmpty()) {
            System.out.println("  (no matching tasks)");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
        }
    }

    /** Displays tasks occurring on a date. */
    public void showTasksOnDate(LocalDate date, ArrayList<Task> tasks) {
        System.out.println("Tasks on " + DateTimeParser.format(date.atStartOfDay()) + ":");
        if (tasks.isEmpty()) {
            System.out.println("  (no deadlines or events)");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
        }
    }

    /** Shows a successful task addition. */
    public void showAdded(Task task, int count) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + count + " tasks in the list.");
    }

    /** Shows a successful completion update. */
    public void showMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /** Shows a successful incomplete update. */
    public void showUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /** Shows a successful deletion. */
    public void showDeleted(Task task, int remaining) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + remaining + " tasks in the list.");
    }

    /** Separates responses after a command. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    @Override
    public void close() {
        scanner.close();
    }
}
