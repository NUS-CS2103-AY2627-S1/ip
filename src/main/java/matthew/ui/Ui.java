package matthew.ui;

import java.util.ArrayList;
import java.util.Scanner;

import matthew.task.Task;

/** Handles Matthew's console input and output. */
public class Ui {
    private static final String LINE =
            "____________________________________________________________";

    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Matthew's welcome banner and greeting. */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(" __  __       _   _   _                   ");
        System.out.println("|  \\/  | __ _| |_| |_| |__   _____      __");
        System.out.println("| |\\/| |/ _` | __| __| '_ \\ / _ \\ \\ /\\ / /");
        System.out.println("| |  | | (_| | |_| |_| | | |  __/\\ V  V / ");
        System.out.println("|_|  |_|\\__,_|\\__|\\__|_| |_|\\___| \\_/\\_/  ");
        System.out.println("Hello! I'm Matthew.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Returns the next command entered on standard input.
     *
     * @return Next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays a separator line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Displays Matthew's goodbye message. */
    public void showGoodbye() {
        System.out.println("Goodbye! Have a nice day!");
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    /** Displays an error message when saved tasks cannot be loaded. */
    public void showLoadingError() {
        System.out.println(
                "OOPS!!! I couldn't load the saved tasks.");
    }

    /**
     * Displays the supplied tasks with one-based list numbers.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println(
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task Marked task.
     */
    public void showMarkedTask(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task Unmarked task.
     */
    public void showUnmarkedTask(Task task) {
        System.out.println(
                "OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println(
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays tasks occurring on a specified date.
     *
     * @param date Date label to display.
     * @param tasks Tasks occurring on the date.
     */
    public void showTasksOn(String date, ArrayList<Task> tasks) {
        System.out.println("Tasks occurring on " + date + ":");

        if (tasks.isEmpty()) {
            System.out.println("No deadlines or events found.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
        }
    }

    /**
     * Displays tasks whose descriptions contain a searched keyword.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(ArrayList<Task> tasks) {
        System.out.println("Here are the matching tasks in your list:");

        if (tasks.isEmpty()) {
            System.out.println("No matching tasks found.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
        }
    }
}
