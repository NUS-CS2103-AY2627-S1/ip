package peter.ui;

import java.util.List;
import java.util.Scanner;

import peter.task.Task;

/**
 * Handle all UI issues.
 */
public class Ui {
    private static final String LINE = "    ____________________________________________________________";
    private static final String LOGO = "____________________________________________________________\n"
            + " ____  _____ _____ _____ ____  \n"
            + "|  _ \\| ____|_   _| ____|  _ \\ \n"
            + "| |_) |  _|   | | |  _| | |_) |\n"
            + "|  __/| |___  | | | |___|  _ < \n"
            + "|_|   |_____| |_| |_____|_| \\_\\\n";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Show welcome to user.
     */
    public void showWelcome() {
        System.out.println(LOGO);
        System.out.println(" Welcome aboard—I'm Peter, your task navigator.");
        System.out.println(" What shall we chart today?");
        showLine();
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public void showError(String message) {
        System.out.println("     " + message);
    }

    /**
     * Prints tasks found on a specific date.
     *
     * @param tasks List of tasks matching the date.
     */
    public void showTasksOnDate(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("     No tasks are charted for that date.");
            return;
        }
        System.out.println("     Here's the route for that date:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints the list of tasks matching a search keyword.
     *
     * @param matchingTasks List of tasks that matched the search query.
     */
    public void showFoundTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            System.out.println("     I searched the map, but found no tasks matching that keyword.");
            return;
        }
        System.out.println("     These tasks match your search:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Read one command from user.
     */
    public String readCommand() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "";
    }

    /**
     * Prints a response while preserving indentation for each line.
     *
     * @param message Response to print.
     */
    public void showMessage(String message) {
        for (String line : message.split("\\R", -1)) {
            System.out.println("     " + line);
        }
    }

    public void close() {
        scanner.close();
    }
}
