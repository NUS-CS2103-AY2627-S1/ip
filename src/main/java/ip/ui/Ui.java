package ip.ui;

import java.util.List;
import java.util.Scanner;

import ip.collection.TaskList;
import ip.model.Task;

/** Handles command-line input and output for Tabby. */
public class Ui {

    private static final String DIVIDER = "    ____________________________________________________________";
    private final Scanner scanner;

    /** Creates a command-line interface backed by standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Reads and trims one command, returning {@code bye} at end of input. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "bye";
    }

    /** Displays the application's welcome message. */
    public void showWelcome() {
        System.out.println(" _____    _    ____   ____  __   __");
        System.out.println("|_   _|  / \\  | __ ) | __ ) \\ \\ / /");
        System.out.println("  | |   / _ \\ |  _ \\ |  _ \\  \\ V / ");
        System.out.println("  | |  / ___ \\| |_) || |_) |  | |  ");
        System.out.println("  |_| /_/   \\_\\____/ |____/   |_|  ");
        showLine();
        System.out.println("     Hello! I'm Tabby. How can I help you?");
        showLine();
        System.out.println();
    }

    /** Displays the standard output divider. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /** Displays a user-facing error message. */
    public void showError(String message) {
        showLine();
        System.out.println("     OOPS!!! " + message);
        showLine();
    }

    /** Displays a warning when saved tasks cannot be loaded. */
    public void showLoadingError() {
        System.out.println("     Warning: Could not load saved tasks. Starting with an empty list.");
    }

    /** Displays the available commands and their expected syntax. */
    public void showHelp() {
        showLine();
        System.out.println("     Available commands:");
        System.out.println("       list                              List all tasks");
        System.out.println("       find KEYWORD                      Find matching tasks");
        System.out.println("       todo DESCRIPTION                  Add a todo");
        System.out.println("       deadline DESCRIPTION /by DATE     Add a deadline");
        System.out.println("       event DESCRIPTION /from DATE /to DATE");
        System.out.println("                                         Add an event");
        System.out.println("       mark NUMBER                       Mark a task as done");
        System.out.println("       unmark NUMBER                     Mark a task as not done");
        System.out.println("       delete NUMBER                     Delete a task");
        System.out.println("       help                              Show this help message");
        System.out.println("       bye                               Exit Tabby");
        showLine();
    }

    /** Displays the exit message. */
    public void showBye() {
        showLine();
        System.out.println("     Bye. Hope to see you again soon!");
        showLine();
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        showLine();
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    /** Displays confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        showLine();
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    /** Displays confirmation that a task was marked done. */
    public void showMarkedDone(Task task) {
        showLine();
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + task);
        showLine();
    }

    /** Displays confirmation that a task was marked not done. */
    public void showMarkedNotDone(Task task) {
        showLine();
        System.out.println("     OK, I've marked this task as not done yet:");
        System.out.println("       " + task);
        showLine();
    }

    /** Displays every task in the supplied list. */
    public void showTaskList(TaskList tasks) {
        showLine();
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
        showLine();
    }

    /**
     * Displays tasks whose descriptions match the supplied keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showLine();
        System.out.println("     Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + matchingTasks.get(i));
        }
        showLine();
    }

    /** Closes the command-line input scanner. */
    public void close() {
        scanner.close();
    }
}
