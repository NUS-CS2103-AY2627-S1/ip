package habpyduck.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import habpyduck.task.Task;

/**
 * Handles all direct interactions with the user through the console.
 */
public class Ui {
    private static final String CHATBOT_NAME = "HabpyDuck";
    private static final String BYE_MESSAGE = "Bye friend. Hope to see you again soon!";
    private static final String SEPARATOR = "____________________________________________________________";

    private final PrintStream output;
    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this(System.out, new Scanner(System.in));
    }

    /**
     * Creates a UI that writes messages to the given output stream.
     *
     * @param output the stream used for messages.
     */
    public Ui(PrintStream output) {
        this(output, null);
    }

    private Ui(PrintStream output, Scanner scanner) {
        this.output = output;
        this.scanner = scanner;
    }

    /**
     * Prints the greeting shown when the chatbot starts.
     */
    public void showWelcome() {
        String banner = " _   _       _                 ____             _    \n"
                + "| | | | __ _| |__  _ __  _   _|  _ \\ _   _  ___| | __\n"
                + "| |_| |/ _` | '_ \\| '_ \\| | | | | | | | | |/ __| |/ /\n"
                + "|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < \n"
                + "|_| |_|\\__,_|_.__/| .__/ \\__, |____/ \\__,_|\\___|_|\\_\\\n"
                + "                  |_|    |___/                       \n";
        showSeparator();
        output.print(banner);
        showLines("Hi friend! I'm " + CHATBOT_NAME + ".",
                "What can I do for you today?");
        showSeparator();
    }

    /**
     * Prints the farewell message shown when the chatbot exits.
     */
    public void showBye() {
        showSeparator();
        showLines(BYE_MESSAGE);
        showSeparator();
    }

    /**
     * Returns the farewell text without console separator lines.
     *
     * @return the farewell text.
     */
    public String getByeMessage() {
        return BYE_MESSAGE;
    }

    /**
     * Returns whether there is another command available to read.
     *
     * @return true if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner != null && scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     *
     * @return the command entered by the user.
     */
    public String readCommand() {
        assert scanner != null : "Scanner should be available before reading console input";
        return scanner.nextLine();
    }

    /**
     * Prints the standard separator line.
     */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /**
     * Prints an error message.
     *
     * @param message the message to show.
     */
    public void showError(String message) {
        showLines(message);
    }

    /**
     * Prints all tasks in the task list.
     *
     * @param tasks the tasks to show.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        showNumberedTasks("Here are the tasks in your list, friend:", tasks);
    }

    /**
     * Prints tasks with descriptions that match the user's search keyword.
     *
     * @param tasks the matching tasks to show.
     */
    public void showMatchingTasks(ArrayList<Task> tasks) {
        showNumberedTasks("I found these matching tasks for you, friend:", tasks);
    }

    /**
     * Prints tasks that have a searched tag.
     *
     * @param tasks the tagged tasks to show.
     */
    public void showTaggedTasks(ArrayList<Task> tasks) {
        showNumberedTasks("I found these tasks with that tag, friend:", tasks);
    }

    /**
     * Prints a header followed by a numbered task list.
     *
     * @param header the heading shown before the tasks.
     * @param tasks the tasks to show.
     */
    private void showNumberedTasks(String header, ArrayList<Task> tasks) {
        output.println(header);
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints the standard message after adding a task.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks now in the list.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLines("Got it, friend. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints the standard message after marking a task as done.
     *
     * @param task the task that was marked.
     */
    public void showTaskMarked(Task task) {
        showLines("YAY GOOD JOB!!! I've marked this task as done, friend:",
                "  " + task);
    }

    /**
     * Prints the standard message after marking a task as not done.
     *
     * @param task the task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        showLines("OK, friend. I've marked this task as not done yet:",
                "  " + task);
    }

    /**
     * Prints the standard message after deleting a task.
     *
     * @param task the task that was deleted.
     * @param taskCount the number of tasks now in the list.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showLines("Got it, friend. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints the standard message after adding tags to a task.
     *
     * @param task the task that was tagged.
     */
    public void showTaskTagged(Task task) {
        showLines("Got it, friend. I've tagged this task:",
                "  " + task);
    }

    /**
     * Prints the standard message after removing a tag from a task.
     *
     * @param task the task that was untagged.
     */
    public void showTaskUntagged(Task task) {
        showLines("Got it, friend. I've removed that tag from this task:",
                "  " + task);
    }

    private void showLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
