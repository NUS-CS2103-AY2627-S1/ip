package michael;

import task.Task;

import java.util.List;
import java.util.Scanner;

/**
 * Handles interactions between michael.Michael and the user.
 */
public class Ui {
    /** The divider used to separate chatbot messages. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Reads commands entered by the user. */
    private final Scanner input;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        input = new Scanner(System.in);
    }

    /** Displays michael.Michael's welcome message. */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println("🌌 Welcome to Mission Control — your space-themed task management app.");
        System.out.println("Here, your tasks are missions. How shall we plan your next launch?");
        System.out.println(DIVIDER);
    }

    /**
     * Reads the next command from the user.
     *
     * @return the next trimmed command, or {@code null} when there is no more input
     */
    public String readCommand() {
        return input.hasNextLine() ? input.nextLine().trim() : null;
    }

    /** Displays the current tasks. */
    public void showTaskList(List<Task> tasks) {
        System.out.println(DIVIDER);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        System.out.println(DIVIDER);
    }


    /**
     * Displays a confirmation after a task is marked as done.
     *
     * @param task the completed task
     */
    public void showTaskMarked(Task task) {
        System.out.println(DIVIDER);
        System.out.println("Yay! You have finished this task:");
        System.out.println("   [X] " + task.getDescription());
        System.out.println(DIVIDER);
    }

    /**
     * Displays a confirmation after a task is unmarked.
     *
     * @param task the task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(DIVIDER);
        System.out.println("This task is no longer marked as complete:");
        System.out.println("   [ ] " + task.getDescription());
        System.out.println(DIVIDER);
    }

    /**
     * Displays a confirmation after a task is deleted.
     *
     * @param task the deleted task
     * @param remainingTaskCount the number of tasks remaining
     */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        System.out.println(DIVIDER);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + remainingTaskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays a confirmation after a task is added.
     *
     * @param task the newly added task
     * @param taskCount the number of tasks in the list
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays an error message using michael.Michael's standard message border.
     *
     * @param message the error explanation
     */
    public void showError(String message) {
        System.out.println(DIVIDER);
        System.out.println(" " + message);
        System.out.println(DIVIDER);
    }

    /** Displays the error shown when saved tasks cannot be loaded. */
    public void showLoadingError(String message) {
        showError("I could not load your saved tasks: " + message);
    }

    /** Displays michael.Michael's farewell message. */
    public void showGoodbye() {
        System.out.println(DIVIDER);
        System.out.println("Bye. Hope we meet again!");
        System.out.println(DIVIDER);
    }
    /**
     * Displays the tasks matching a search keyword.
     *
     * @param matchingTasks The list of tasks that match the query.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println(DIVIDER);
        if (matchingTasks.isEmpty()) {
            System.out.println(" No matching tasks found in your list.");
        } else {
            System.out.println(" Here are the matching tasks in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + matchingTasks.get(i));
            }
        }
        System.out.println(DIVIDER);
    }
}
