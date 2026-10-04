package happy.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import happy.task.Task;
import happy.task.TaskList;

/**
 * Handles all user interface interactions for the Happy chatbot.
 * Responsible for formatting messages, displaying output, and reading user input.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = "  _    _   _   ___  ___  __   __\n"
            + " | |  | | /_\\ | _ \\| _ \\ \\ \\ / /\n"
            + " | __ | |/ _ \\|  _/|  _/  \\ V / \n"
            + " |_||_|_/_/ \\_\\_|  |_|     |_|  \n";

    private final Scanner scanner;

    /**
     * Constructs a new Ui instance with System.in input scanner.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads a line of command input from the user.
     *
     * @return Trimmed input string entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints the horizontal divider line.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays the initial welcome banner and greeting.
     */
    public void showWelcome() {
        showLine();
        System.out.print(BANNER);
        System.out.println("Hellllloooooooooo! I'm Happy, the happiest chatbot in the world!");
        System.out.println("What can I do for you today?");
        showLine();
    }

    /**
     * Displays the farewell message upon exit.
     */
    public void showGoodbye() {
        showLine();
        System.out.println("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Displays an error message formatted within dividers.
     *
     * @param message Detailed error message.
     */
    public void showError(String message) {
        showLine();
        System.out.println(" " + message);
        showLine();
    }

    /**
     * Displays a warning message if task loading from disk fails.
     */
    public void showLoadingError() {
        showLine();
        System.out.println(" Warning: Could not load tasks from disk. Starting with an empty task list.");
        showLine();
    }

    /**
     * Displays confirmation message when a new task is added.
     *
     * @param task Task object that was added.
     * @param totalTasks Total number of tasks currently in the list.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        showLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalTasks + " tasks in the list.");
        showLine();
    }

    /**
     * Displays confirmation message when a task is removed.
     *
     * @param task Task object that was removed.
     * @param totalTasks Total number of tasks remaining in the list.
     */
    public void showTaskRemoved(Task task, int totalTasks) {
        showLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalTasks + " tasks in the list.");
        showLine();
    }

    /**
     * Displays confirmation message when a task is marked as done.
     *
     * @param task Task object that was marked.
     */
    public void showTaskMarked(Task task) {
        showLine();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        showLine();
    }

    /**
     * Displays confirmation message when a task is marked as not done yet.
     *
     * @param task Task object that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        showLine();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
        showLine();
    }

    /**
     * Displays all tasks currently in the TaskList.
     *
     * @param taskList TaskList containing current tasks.
     */
    public void showTaskList(TaskList taskList) {
        showLine();
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println((i + 1) + "." + taskList.get(i));
        }
        showLine();
    }

    /**
     * Displays tasks matching a specific target date.
     *
     * @param date Target date filtered on.
     * @param matchingTasks List of matching tasks.
     */
    public void showTasksOnDate(LocalDate date, ArrayList<Task> matchingTasks) {
        showLine();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        System.out.println("Here are the tasks occurring on " + formattedDate + ":");
        if (matchingTasks.isEmpty()) {
            System.out.println("  No tasks found for this date.");
        } else {
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println((i + 1) + "." + matchingTasks.get(i));
            }
        }
        showLine();
    }

    /**
     * Displays all tasks matching a keyword search query.
     *
     * @param matchingTasks List of matching tasks found.
     */
    public void showFoundTasks(ArrayList<Task> matchingTasks) {
        showLine();
        if (matchingTasks.isEmpty()) {
            System.out.println(" No matching tasks found in your list.");
        } else {
            System.out.println(" Here are the matching tasks in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + matchingTasks.get(i));
            }
        }
        showLine();
    }

    /**
     * Closes the input scanner resource.
     */
    public void close() {
        scanner.close();
    }
}
