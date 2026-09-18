package waffles;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all input and output for the Waffles chatbot.
 */
public class Ui {
    /** The divider used to separate chatbot messages. */
    static final String SEPARATOR = "____________________________________________________________";

    /** Reads commands entered through standard input. */
    private final Scanner scanner;

    /** Receives messages displayed by this user interface. */
    private final PrintStream output;

    /** Creates a user interface connected to standard input. */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a user interface that writes to the specified output stream.
     *
     * @param output the stream to receive chatbot messages
     */
    public Ui(PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /** Displays the chatbot's welcome message. */
    public void showWelcome() {
        String banner = " __        __    _      _____ _____ _      _____  ____\n"
                + " \\ \\      / /   / \\    |  ___|  ___| |    | ____|/ ___|\n"
                + "  \\ \\/\\ / /   / _ \\   | |_  | |_  | |    |  _|  \\___ \\ \n"
                + "   \\ V  V /   / ___ \\  |  _| |  _| | |___ | |___  ___) |\n"
                + "    \\_/\\_/   /_/   \\_\\ |_|   |_|   |_____||_____||____/";

        showDivider();
        output.println(banner);
        output.println("Hello! I'm Waffles, your cheerful kitchen-sidekick.");
        output.println("Let's get your day cooking. What can I do for you?");
        showDivider();
    }

    /**
     * Returns whether another command is available.
     *
     * @return whether standard input has another line
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims one command.
     *
     * @return the next command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the standard divider. */
    public void showDivider() {
        output.println(SEPARATOR);
    }

    /** Displays the goodbye message. */
    public void showGoodbye() {
        output.println("Keep your plans cooking. Waffles out!");
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks the task list to display
     */
    public void showTaskList(TaskList tasks) {
        showDivider();
        output.println("Here are the tasks in your list:");
        showNumberedTasks(tasks.asList());
        showDivider();
    }

    /**
     * Displays the tasks returned by a keyword search.
     *
     * @param matchingTasks the tasks whose descriptions matched the keyword
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showDivider();
        output.println("Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            output.println("No matching tasks found.");
        } else {
            showNumberedTasks(matchingTasks);
        }
        showDivider();
    }

    /**
     * Displays an error message.
     *
     * @param message the problem and correction hint
     */
    public void showError(String message) {
        showDivider();
        output.println("Oops, " + message);
        showDivider();
    }

    /**
     * Displays confirmation after adding a task.
     *
     * @param task the newly added task
     * @param taskCount the updated task count
     */
    public void showTaskAdded(Task task, int taskCount) {
        showDivider();
        String taskType = getTaskType(task);
        output.println("Got it. I've added this " + taskType + ":");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
        showDivider();
    }

    /** Displays a warning when a new scheduled task overlaps existing tasks. */
    public void showSchedulingWarning(List<Task> conflicts) {
        showDivider();
        output.println("Warning: this task clashes with existing scheduled tasks:");
        conflicts.forEach(conflict -> output.println("  " + conflict));
        showDivider();
    }

    /** Displays tasks with their one-based positions. */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Returns the user-facing name for a task subtype. */
    private String getTaskType(Task task) {
        if (task instanceof Event) {
            return "event";
        }
        if (task instanceof Deadline) {
            return "deadline";
        }
        return "task";
    }

    /**
     * Displays confirmation after changing a task's completion status.
     *
     * @param task the updated task
     * @param isDone whether the task was marked done
     */
    public void showMarkResult(Task task, boolean isDone) {
        showDivider();
        if (isDone) {
            output.println("Nice! I've marked this task as done:");
        } else {
            output.println("OK, I've marked this task as not done yet:");
        }
        output.println("  " + task);
        showDivider();
    }

    /**
     * Displays confirmation after deleting a task.
     *
     * @param task the deleted task
     * @param taskCount the remaining task count
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showDivider();
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
        showDivider();
    }
}
