package mochi.ui;

import java.io.PrintStream;
import java.util.Scanner;

import mochi.task.Task;
import mochi.task.TaskList;

/**
 * Handles console input and output for Mochi.
 */
public class Ui {
    /** Shared greeting keeps the desktop and console personalities consistent. */
    public static final String WELCOME_MESSAGE = "Hello! I'm Mochi, your little pocket of calm.\n"
            + "Let's take your tasks one bite at a time.";
    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a console UI that reads standard input.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI backed by the supplied input and output streams.
     *
     * @param scanner command source
     * @param output response destination
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Checks whether another complete command is available.
     *
     * @return true when another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next complete command.
     *
     * @return command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Mochi's greeting.
     */
    public void showWelcome() {
        showLines(SEPARATOR, WELCOME_MESSAGE, SEPARATOR);
    }

    /**
     * Displays the response separator.
     */
    public void showLine() {
        output.println(SEPARATOR);
    }

    /**
     * Displays Mochi's farewell.
     */
    public void showGoodbye() {
        output.println("Rest well! Mochi will be here for your next little step.");
    }

    /**
     * Displays all tasks with one-based numbering.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        output.println("Here are the tasks in your list:");
        showTasks(tasks);
    }

    /**
     * Displays tasks matching a find command.
     *
     * @param tasks matching tasks to display
     */
    public void showMatchingTasks(TaskList tasks) {
        output.println("Here are the matching tasks in your list:");
        showTasks(tasks);
    }

    private void showTasks(TaskList tasks) {
        if (tasks.size() == 0) {
            output.println("Nothing here yet. A little breathing room!");
        }
        int index = 1;
        for (Task task : tasks) {
            output.println(index + "." + task);
            index++;
        }
    }

    /**
     * Displays confirmation that a task was marked done.
     *
     * @param task task whose status changed
     */
    public void showMarked(Task task) {
        showLines("Sweet! One bite done. I've marked this task as done:", "  " + task);
    }

    /**
     * Displays confirmation that a task was marked not done.
     *
     * @param task task whose status changed
     */
    public void showUnmarked(Task task) {
        showLines("No rush. Back on the plate, marked as not done yet:", "  " + task);
    }

    /**
     * Displays a removed task and the remaining count.
     *
     * @param task removed task
     * @param taskCount number of tasks remaining
     */
    public void showDeleted(Task task, int taskCount) {
        showLines("A little less on your plate. I've removed this task:", "  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays an added task and the updated count.
     *
     * @param task added task
     * @param taskCount number of tasks after addition
     */
    public void showAdded(Task task, int taskCount) {
        showLines("Tucked away! I've added this task:", "  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays a recoverable command or storage error.
     *
     * @param message user-friendly error explanation
     */
    public void showError(String message) {
        output.println("Oops! " + message);
    }

    /**
     * Displays a grammatically correct task count.
     *
     * @param taskCount number of tasks
     */
    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println("Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /**
     * Prints any number of response lines in their supplied order.
     *
     * @param lines response lines to print
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
