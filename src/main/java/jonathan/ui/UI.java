package jonathan.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import jonathan.task.Priority;
import jonathan.task.Task;

/**
 * Handles all console input and output formatting for the chatbot.
 */
public class UI {
    private static final String BANNER = "     _             _   _\n"
            + "    | | ___  _ __ | |_| |__   __ _ _ __\n"
            + " _  | |/ _ \\| '_ \\| __| '_ \\ / _` | '_ \\\n"
            + "| |_| | (_) | | | | |_| | | | (_| | | | |\n"
            + " \\___/ \\___/|_| |_|\\__|_| |_|\\__,_|_| |_|\n";
    private static final String LINE = "___________________________________";
    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a console UI that writes output to standard output. */
    public UI() {
        this(System.out);
    }

    /**
     * Creates a UI that writes output to the provided stream.
     *
     * @param output stream that receives formatted UI messages
     */
    public UI(PrintStream output) {
        this.scanner = new Scanner(System.in);
        this.output = output;
    }

    /** Displays the chatbot banner and welcome message. */
    public void showWelcome() {
        output.println(BANNER);
        output.println(LINE);
        output.println("Hello! I'm Griddel.");
        output.println("What can I do for you?");
        output.println(LINE);
    }

    /** Displays the farewell message. */
    public void showGoodbye() {
        output.println(LINE);
        output.println("Bye. Hope to see you again soon!");
        output.println(LINE);
    }

    /**
     * Displays all current tasks in the user's task list.
     *
     * @param tasks     The array containing the tasks to be displayed.
     * @param itemCount The number of active tasks currently in the array.
     */
    public void showTaskList(Task[] tasks, int itemCount) {
        output.println(LINE);
        output.println("Here are the tasks in your list:");
        for (int i = 0; i < itemCount; i++) {
            output.printf("%d.%s%n", i + 1, tasks[i]);
        }
        output.println(LINE);
    }

    /**
     * Displays a confirmation message after successfully marking a task as done.
     *
     * @param task The task that was marked as done.
     */
    public void showMarked(Task task) {
        output.println(LINE);
        output.println("Nice! I've marked this task as done:");
        output.println(task);
        output.println(LINE);
    }

    /**
     * Displays a confirmation message after successfully marking a task as not done.
     *
     * @param task The task that was marked as not done.
     */
    public void showUnmarked(Task task) {
        output.println(LINE);
        output.println("OK, I've marked this task as not done yet:");
        output.println(task);
        output.println(LINE);
    }

    /**
     * Displays a consistently formatted error message to the console.
     *
     * @param message The specific error explanation to be shown to the user.
     */
    public void showError(String message) {
        output.println(LINE);
        output.println("Error: " + message);
        output.println(LINE);
    }

    /**
     * Displays a confirmation message after successfully adding a new task.
     *
     * @param task      The task that was just added.
     * @param itemCount The updated total number of tasks in the list.
     */
    public void showAdded(Task task, int itemCount) {
        output.println(LINE);
        output.println("Got it! I added this task:");
        output.println("  " + task);
        output.printf("Now you have %d tasks in the list.%n", itemCount);
        output.println(LINE);
    }

    /**
     * Displays a confirmation message after successfully deleting a task.
     *
     * @param task      The task that was removed from the list.
     * @param itemCount The updated total number of tasks remaining in the list.
     */
    public void showDeleted(Task task, int itemCount) {
        output.println(LINE);
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        output.printf("Now you have %d tasks in the list.%n", itemCount);
        output.println(LINE);
    }

    /**
     * Searches for and displays all tasks that occur on a specific date.
     *
     * @param dateString The target date to search for, in {@code yyyy-mm-dd} format.
     * @param tasks      The array containing the user's tasks.
     * @param itemCount  The number of active tasks currently in the array.
     */
    public void showActivitiesOn(String dateString, Task[] tasks, int itemCount) {
        try {
            LocalDate targetDate = LocalDate.parse(dateString);
            output.println(LINE);
            output.println("Here are the tasks occurring on "
                    + targetDate.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ":");

            int matchCount = 0;
            for (int i = 0; i < itemCount; i++) {
                if (tasks[i].isOccuringOn(targetDate)) {
                    matchCount++;
                    output.printf("%d.%s%n", matchCount, tasks[i]);
                }
            }

            if (matchCount == 0) {
                output.println("  No tasks found for this date.");
            }
            output.println(LINE);
        } catch (DateTimeParseException exception) {
            showError("Invalid date format. Please use yyyy-mm-dd (e.g., 2026-08-27).");
        }
    }

    /**
     * Searches for and displays all tasks that contain the specified keyword in their description.
     *
     * @param keyword   The search term provided by the user.
     * @param tasks     The array containing the user's tasks.
     * @param itemCount The number of active tasks currently in the array.
     */
    public void showFoundTasks(String keyword, Task[] tasks, int itemCount) {
        output.println(LINE);
        output.println("Here are the matching tasks in your list:");

        List<Task> matchingTasks = Arrays.stream(tasks, 0, itemCount)
                        .filter(task-> task.toString().contains(keyword))
                        .toList();

        for (int i = 0; i < matchingTasks.size(); i++) {
            output.printf("%d.%s%n", i + 1, matchingTasks.get(i));
        }

        if (matchingTasks.isEmpty()) {
            output.println(" No matching tasks found.");
        }

        output.println(LINE);
    }

    /**
     * Reads the next line of user input from the console.
     *
     * @return The raw command string entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a confirmation after updating a task priority.
     *
     * @param task task whose priority was updated
     */
    public void showPriorityUpdated(Task task) {
        output.println(LINE);
        output.println("Updated the priority of this task:");
        output.println("  " + task);
        output.println(LINE);
    }

    /**
     * Displays tasks matching a requested priority.
     *
     * @param priority priority used for filtering
     * @param matchingTasks tasks that have the requested priority
     */
    public void showFilteredTasks(Priority priority, List<Task> matchingTasks) {
        output.println(LINE);
        output.println("Here are the " + priority.getLabel() + "-priority tasks:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.printf("%d.%s%n", i + 1, matchingTasks.get(i));
        }
        if (matchingTasks.isEmpty()) {
            output.println("  No tasks found with this priority.");
            output.println("  Assign one with: priority <task number> <level>");
        }
        output.println(LINE);
    }

    /**
     * Displays the task list after it has been sorted by priority.
     *
     * @param sortedTasks tasks ordered by priority for display
     */
    public void showSortedTasks(List<Task> sortedTasks) {
        output.println(LINE);
        output.println("Tasks sorted by priority (high to low):");
        for (int i = 0; i < sortedTasks.size(); i++) {
            output.printf("%d.%s%n", i + 1, sortedTasks.get(i));
        }
        output.println(LINE);
    }

}
