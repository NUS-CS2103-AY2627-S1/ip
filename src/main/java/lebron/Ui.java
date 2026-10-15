package lebron;

import java.util.Scanner;

/**
 * Handles interactions between the user and the Lebron chatbot.
 * Displays messages and reads commands entered by the user.
 */
public class Ui {

    private final Scanner scanner;

    /**
     * Creates a new user interface for reading console input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the greeting message when the chatbot starts.
     */
    public void showGreeting() {
        System.out.println("Hello! I'm Lebron");
        System.out.println("What can I do for you?");
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays all tasks in the given task list.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i)
            );
        }
    }

    /**
     * Displays a message confirming that a task has been added.
     *
     * @param task task that was added
     */
    public void showAddedTask(Task task) {
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
    }

    /**
     * Displays a message confirming that a task has been marked as done.
     *
     * @param task task that was marked as done
     */
    public void showMarkedTask(Task task) {
        System.out.println(
                "Nice! I've marked this task as done:"
        );
        System.out.println(task);
    }

    /**
     * Displays the deleted task and the number of remaining tasks.
     *
     * @param task task that was deleted
     * @param remaining number of tasks remaining in the list
     */
    public void showDeletedTask(Task task, int remaining) {
        System.out.println(
                "Alright, I've removed this task:"
        );
        System.out.println(task);

        System.out.println(
                "Now you have "
                        + remaining
                        + " tasks in the list."
        );
    }

    /**
     * Displays the given message to the user.
     *
     * @param message message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the goodbye message when the chatbot exits.
     */
    public void showGoodbye() {
        System.out.println(
                "That's game. See you next time!"
        );
    }

    /**
     * Displays the tasks that match the user's search keyword.
     *
     * @param tasks task list containing the matching tasks
     */
    public void showMatchingTasks(TaskList tasks) {
        System.out.println("Here are the matching tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Closes the scanner used to read user input.
     */
    public void close() {
        scanner.close();
    }
}
