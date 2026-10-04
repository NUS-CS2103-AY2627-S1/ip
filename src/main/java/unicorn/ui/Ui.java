package unicorn.ui;

import java.util.List;
import java.util.Scanner;

import unicorn.task.Task;
import unicorn.task.TaskList;

/**
 * Handles all console input and output for the chatbot.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message.
     *
     * @param banner chatbot artwork to display before the greeting
     */
    public void showWelcome(String banner) {
        System.out.println(banner);
        System.out.println("Hello! I'm Prisma, your wise tech unicorn.");
        System.out.println("What quest can I help you with?");
    }

    /**
     * Reads one command entered by the user.
     *
     * @return the entered command
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays every task with its one-based list number.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + ": " + tasks.get(index));
        }
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks tasks that match the user's search
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println("Here are the matching tasks in your list:");
        for (int index = 0; index < matchingTasks.size(); index++) {
            System.out.println((index + 1) + "." + matchingTasks.get(index));
        }
    }

    /**
     * Displays an error message.
     *
     * @param message error explanation
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Confirms that a task was marked complete.
     *
     * @param task completed task
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task);
    }

    /**
     * Confirms that a task was marked incomplete.
     *
     * @param task incomplete task
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(task);
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task deleted task
     * @param taskCount number of remaining tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that a task was added.
     *
     * @param task added task
     * @param taskCount number of tasks after adding
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays help for an unrecognised command.
     */
    public void showHelp() {
        System.out.println(
                "I don't understand that command. You may add tasks by specifying todo, "
                        + "event, or deadline at the start, or view your tasks by entering 'list' or 'find'. "
                        + "You may also mark, unmark, or delete your tasks by specifying "
                        + "'mark', 'unmark', or 'delete' followed by the index of the task.");
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }
}
