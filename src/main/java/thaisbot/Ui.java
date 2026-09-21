package thaisbot;

import java.time.LocalDate;
import java.util.Scanner;

import thaisbot.task.Task;
import thaisbot.task.TaskList;

/**
 * Handles input/output with the user. Responsible for printing messages to the console and
 * reading user input.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Constructs a Ui that reads from System.in.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Shows the welcome message when the application starts.
     */
    public void showWelcome() {
        System.out.println("Hello! I'm Thai's Bot. Let's make the chaos manageable.");
        System.out.println("Bring me tasks, deadlines, events, and tags.");
    }

    /**
     * Reads a line of input from the user.
     * @return trimmed user input line
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Shows the goodbye message when the application exits.
     */
    public void showBye() {
        System.out.println("All done for now. Come back whenever the stack grows.");
    }

    /**
     * Shows an error message to the user.
     * @param message the error description
     */
    public void showError(String message) {
        System.out.println("Oops — Thai's Bot got tangled up: " + message);
    }

    /**
     * Displays the full task list.
     * @param tasks the task list to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here's your task lineup:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays a message after a task is added.
     * @param task the task that was added
     * @param taskCount current number of tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Nice! I've tucked this one into the lineup:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a message after a task is marked done.
     * @param task the task that was marked
     */
    public void showTaskMarkedDone(Task task) {
        System.out.println("Great job. This task is officially done:");
        System.out.println("  " + task);
    }

    /**
     * Displays a message after a task is unmarked.
     * @param task the task that was unmarked
     */
    public void showTaskMarkedNotDone(Task task) {
        System.out.println("No worries. I've put this one back on the list:");
        System.out.println("  " + task);
    }

    /**
     * Displays a message after a task is removed.
     * @param task the task that was removed
     * @param taskCount current number of tasks
     */
    public void showTaskRemoved(Task task, int taskCount) {
        System.out.println("Out it goes:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows deadlines and events that occur on a specific date.
     * @param tasks the task list to search
     * @param date the date to match
     */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        System.out.println("Let's check what's on " + date + ":");
        int shownCount = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                shownCount++;
                // Show the full-list number, so that mark/unmark/delete act on this task.
                System.out.println((i + 1) + "." + task);
            }
        }
        if (shownCount == 0) {
            System.out.println("No deadlines or events found on that date.");
        }
    }

    /**
     * Shows tasks whose descriptions contain the given keyword.
     * @param tasks the task list to search
     * @param keyword the keyword to match
     */
    public void showMatchingTasks(TaskList tasks, String keyword) {
        System.out.println("Here are the matches I found:");
        int shownCount = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.matchesSearch(keyword)) {
                shownCount++;
                // Show the full-list number, so that mark/unmark/delete act on this task.
                System.out.println((i + 1) + "." + task);
            }
        }
        if (shownCount == 0) {
            System.out.println("No matching tasks found.");
        }
    }
}
