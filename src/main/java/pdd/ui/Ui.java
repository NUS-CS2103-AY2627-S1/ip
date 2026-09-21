package pdd.ui;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import pdd.task.Task;

/**
 * Handles all interaction with the user: printing the greeting/goodbye
 * messages, reading command lines from standard input, and printing the
 * responses to each command (task added/removed/listed, errors, etc).
 * Keeping all console I/O here means the rest of the program can work
 * with plain data and doesn't need to know how output is formatted.
 *
 * <p>Where each line goes is pluggable via {@code writer}: the console
 * text UI writes to {@link System#out}, while the JavaFX GUI ({@code
 * pdd.PDD#getResponse}) collects the same lines into a response string
 * instead, letting every {@code Command} keep calling the same
 * {@code showXxx} methods regardless of which UI is in front.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private final Consumer<String> writer;
    private Scanner scanner;

    /** Creates a console-backed Ui: every line is printed to {@link System#out}. */
    public Ui() {
        this(System.out::println);
    }

    /** Creates a Ui that sends every line it would otherwise print to {@code writer} instead. */
    public Ui(Consumer<String> writer) {
        this.writer = writer;
    }

    /** Prints each given line, in order. */
    private void print(String... lines) {
        Arrays.stream(lines).forEach(writer);
    }

    /** Prints the divider line shown before and after every command's response. */
    public void showLine() {
        writer.accept(LINE);
    }

    /** Prints the startup banner and greeting. */
    public void showWelcome() {
        String banner = " ____  ____  ____  \n"
                + "|  _ \\|  _ \\|  _ \\ \n"
                + "| |_) | | | | | | |\n"
                + "|  __/| |_| | |_| |\n"
                + "|_|   |____/|____/ \n";
        showLine();
        print(banner, "Hello! I'm PDD.", "What can I do for you?");
        showLine();
    }

    /** Prints the goodbye message shown when the user exits. */
    public void showGoodbye() {
        writer.accept("Bye. Hope to see you again soon!");
    }

    /** Reads one full line of user input. */
    public String readCommand() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner.nextLine();
    }

    /** Prints an error message, e.g. from a caught {@link PDDException}. */
    public void showError(String message) {
        writer.accept(message);
    }

    /** Prints the full task list, numbered from 1. */
    public void showTaskList(List<Task> tasks) {
        showNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /** Prints the given header followed by each task in the list, numbered from 1. */
    private void showNumberedTasks(String header, List<Task> tasks) {
        writer.accept(header);
        for (int i = 0; i < tasks.size(); i++) {
            writer.accept((i + 1) + "." + tasks.get(i));
        }
    }

    /** Prints the tasks that occur on the given date, numbered from 1. */
    public void showTasksOn(LocalDate date, List<Task> tasks) {
        List<Task> tasksOnDate = tasks.stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toList());
        showNumberedTasks("Here are the tasks on " + date.format(Task.DISPLAY_DATE_FORMAT) + ":", tasksOnDate);
    }

    /** Prints the confirmation shown after a task is marked done. */
    public void showMarked(Task task) {
        print("Nice! I've marked this task as done:", "  " + task);
    }

    /** Prints the confirmation shown after a task is marked not done. */
    public void showUnmarked(Task task) {
        print("OK, I've marked this task as not done yet:", "  " + task);
    }

    /** Prints the confirmation shown after a task is deleted. */
    public void showDeleted(Task task, int remainingCount) {
        print("Noted. I've removed this task:", "  " + task,
                "Now you have " + remainingCount + " tasks in the list.");
    }

    /** Prints the confirmation shown after a task is added. */
    public void showAdded(Task task, int totalCount) {
        print("Got it. I've added this task:", "  " + task,
                "Now you have " + totalCount + " tasks in the list.");
    }

    /** Prints the tasks matching a search keyword, numbered from 1. */
    public void showMatchingTasks(List<Task> matches) {
        showNumberedTasks("Here are the matching tasks in your list:", matches);
    }

    /** Prints the confirmation shown after the task list is sorted, followed by the new order. */
    public void showSorted(List<Task> tasks) {
        showNumberedTasks("Sorted! Here are your tasks, in order:", tasks);
    }
}
