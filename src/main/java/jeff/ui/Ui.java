package jeff.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jeff.task.Task;

/**
 * Handles all interaction with the user: printing Jeff's messages and
 * reading the user's typed input.
 */
public class Ui {
    private static final String INDENT = "    ";
    private static final String DIVIDER = INDENT + "____________________________________________________________";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints a (possibly multi-line) machine message with each line indented,
     * so it is visually distinct from the user's own typed input.
     */
    public void showMessage(String message) {
        for (String line : message.split("\n")) {
            System.out.println(INDENT + line);
        }
    }

    /** Prints the horizontal divider line used to separate turns of the conversation. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints the startup banner followed by {@code greeting}. The greeting is passed in
     * (rather than built here) so the caller (see {@link jeff.Jeff#getWelcomeMessage()})
     * can fold in anything the user needs to know right away, e.g. a warning that their
     * saved tasks failed to load.
     */
    public void showWelcome(String greeting) {
        String banner = "     _  _____  _____  _____ \n"
                + "    | || ____||  ___||  ___|\n"
                + "    | || |__  | |_   | |_   \n"
                + "| |_| ||  __| |  _|  |  _|  \n"
                + " \\___/ |_____||_|    |_|    ";
        showLine();
        showMessage(banner);
        showMessage(greeting);
        showLine();
    }

    /** Builds the greeting shown when Jeff starts up, without the ASCII banner. */
    public String formatGreeting() {
        return "☺ Hello! I'm Jeff, your friendly task-tracking sidekick.\nWhat can I do for you today?";
    }

    /** Prints the farewell message. */
    public void showBye() {
        showMessage(formatBye());
    }

    /** Builds the farewell message. */
    public String formatBye() {
        return "✌ Bye for now! Jeff's clocking off - hope to see you again soon!";
    }

    /** Prints an error message, prefixed the same way for every kind of failure. */
    public void showError(String message) {
        showMessage(formatError(message));
    }

    /**
     * Builds an error message, prefixed the same way for every kind of failure so
     * that both the console and the GUI (see {@code jeff.gui.MainApp}) can reliably
     * recognize an error reply just by checking for the {@code "OOPS!!!"} text.
     */
    public String formatError(String message) {
        return "⚠ OOPS!!! " + message + " Let's give that another shot!";
    }

    /** Prints every given task as a numbered list, in order. */
    public void showTaskList(List<Task> tasks) {
        showMessage(formatTaskList(tasks));
    }

    /** Builds the numbered list of every given task, in order. */
    public String formatTaskList(List<Task> tasks) {
        return formatNumberedTasks("☰ Here's everything on your plate:", tasks);
    }

    /** Prints every given task that matched a "find" search, numbered in order. */
    public void showMatchingTasks(List<Task> tasks) {
        showMessage(formatMatchingTasks(tasks));
    }

    /** Builds the numbered list of every given task that matched a "find" search, in order. */
    public String formatMatchingTasks(List<Task> tasks) {
        return formatNumberedTasks("★ Here's what I dug up for you:", tasks);
    }

    /** Prints every given task, numbered in order, after a "sort" command reordered them. */
    public void showSortedTasks(List<Task> tasks) {
        showMessage(formatSortedTasks(tasks));
    }

    /** Builds the numbered list of every given task, in order, after a "sort" command reordered them. */
    public String formatSortedTasks(List<Task> tasks) {
        return formatNumberedTasks("↕ All tidied up and sorted chronologically by date:", tasks);
    }

    /** Builds the given header followed by each task, numbered from 1 in the given order. */
    private String formatNumberedTasks(String header, List<Task> tasks) {
        String body = IntStream.range(0, tasks.size())
                .mapToObj(i -> "\n" + (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining());
        return header + body;
    }

    /** Prints confirmation that a task was added, e.g. after a "todo"/"deadline"/"event" command. */
    public void showTaskAdded(Task task, int taskCount) {
        showMessage(formatTaskAdded(task, taskCount));
    }

    /** Builds confirmation that a task was added, e.g. after a "todo"/"deadline"/"event" command. */
    public String formatTaskAdded(Task task, int taskCount) {
        return "✚ Got it! I've added this task:\n  " + task + "\nYou now have " + taskCount
                + " tasks in the list. Let's keep the momentum going!";
    }

    /** Prints confirmation that a task was removed, e.g. after a "delete" command. */
    public void showTaskRemoved(Task task, int taskCount) {
        showMessage(formatTaskRemoved(task, taskCount));
    }

    /** Builds confirmation that a task was removed, e.g. after a "delete" command. */
    public String formatTaskRemoved(Task task, int taskCount) {
        return "✖ Done! I've removed this task:\n  " + task + "\nYou now have " + taskCount
                + " tasks in the list. One less thing to worry about!";
    }

    /** Prints confirmation that a task was marked done. */
    public void showTaskMarked(Task task) {
        showMessage(formatTaskMarked(task));
    }

    /** Builds confirmation that a task was marked done. */
    public String formatTaskMarked(Task task) {
        return "✓ Nice work! I've marked this task as done:\n  " + task + "\nAnother one bites the dust!";
    }

    /** Prints confirmation that a task was marked not done. */
    public void showTaskUnmarked(Task task) {
        showMessage(formatTaskUnmarked(task));
    }

    /** Builds confirmation that a task was marked not done. */
    public String formatTaskUnmarked(Task task) {
        return "↺ No worries! I've marked this task as not done yet:\n  " + task + "\nYou'll get to it!";
    }

    /** Reads one line of user input, or {@code null} if there is no more input (e.g. end of stream). */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Releases the underlying input resource; call once when Jeff is shutting down. */
    public void close() {
        scanner.close();
    }
}
