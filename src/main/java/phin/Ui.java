package phin;

import java.util.List;
import java.util.Scanner;

/**
 * Reads console commands and formats all user-facing responses for Phin.
 */
public class Ui {
    /**
     * Creates a console interface that reads commands from standard input.
     */
    public Ui() {
    }

    /**
     * Divider printed between command responses.
     */
    private static final String DIVIDER = "____________________________________________________________";
    /**
     * Reader for commands from standard input.
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Checks whether another command can be read without reaching the end of input.
     *
     * @return True if another input line is available; false at end of input.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command unchanged after {@link #hasNextCommand()} succeeds.
     *
     * @return The next input line, without its line separator.
     * @throws java.util.NoSuchElementException If no input line is available.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the greeting between divider lines.
     */
    public void showWelcome() {
        showLine();
        System.out.println("Phin");
        System.out.println("I'm Phin. Apparently I have to deal with this.");
        System.out.println("What do you want?");
        showLine();
    }

    /**
     * Prints the divider separating command responses.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints the farewell message.
     */
    public void showGoodbye() {
        System.out.println("    Finally. Bye.");
    }

    /**
     * Displays tasks with the one-based numbers used in commands.
     *
     * @param tasks Tasks to display in their current order.
     */
    public void showTasks(List<Task> tasks) {
        System.out.println("    Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("    " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays matches with result numbers, without changing their saved task numbers.
     *
     * @param matches Matching tasks in their original order.
     */
    public void showMatchingTasks(List<Task> matches) {
        System.out.println("    Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            System.out.println("    " + (i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Confirms a status change without modifying the task.
     *
     * @param task Task whose completion status was updated.
     * @param isDone Whether the task was marked completed.
     */
    public void showMarkedTask(Task task, boolean isDone) {
        System.out.println("    Fine. I've marked this task as " + (isDone ? "done" : "not done") + ":");
        System.out.println("      " + task);
    }

    /**
     * Confirms addition and reports the resulting list size.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println("    Fine. I've added this task:");
        System.out.println("      " + task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms deletion and reports the resulting list size.
     *
     * @param task Task that was removed.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println("    Noted. I've removed this task:");
        System.out.println("      " + task);
        showTaskCount(taskCount);
    }

    /**
     * Prints the number of tasks remaining in the list.
     *
     * @param taskCount Current number of tasks.
     */
    private void showTaskCount(int taskCount) {
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints an explanation of a rejected command.
     *
     * @param message Explanation describing the input error.
     */
    public void showError(String message) {
        System.out.println("    Seriously? " + message);
    }

    /**
     * Prints a response produced by the chatbot core.
     *
     * @param response Complete response without a trailing line separator.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Reports that saved tasks could not be loaded and the data file was left unchanged.
     */
    public void showLoadingError() {
        System.out.println("    Couldn't load data/phin.txt. Check the file before restarting;"
                + " it has not been changed.");
    }

    /**
     * Reports that task changes remain in memory because saving failed.
     */
    public void showSavingError() {
        System.out.println("    Couldn't save data/phin.txt. Changes are only in memory; check the data folder.");
    }
}
