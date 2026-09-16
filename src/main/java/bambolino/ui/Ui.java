package bambolino.ui;

import java.util.Scanner;
import java.util.function.Consumer;

import bambolino.task.Task;
import bambolino.task.TaskList;

/**
 * Handles console input and output for Bambolino.
 */
public class Ui {
    /** The line used to separate Bambolino's responses. */
    private static final String DIVIDER = "____________________________________________________________";

    /** The banner displayed when Bambolino starts. */
    private static final String BANNER = " ____                  _           _ _             \n"
            + "| __ )  __ _ _ __ ___ | |__   ___ | (_)_ __   ___  \n"
            + "|  _ \\ / _` | '_ ` _ \\| '_ \\ / _ \\| | | '_ \\ / _ \\ \n"
            + "| |_) | (_| | | | | | | |_) | (_) | | | | | | (_) |\n"
            + "|____/ \\__,_|_| |_| |_|_.__/ \\___/|_|_|_| |_|\\___/ \n";

    /** The source of user commands. */
    private final Scanner scanner;
    private final Consumer<String> output;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this(System.out::println);
    }

    /** Creates a user interface that sends each complete line to the supplied output. */
    public Ui(Consumer<String> output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /** Displays a line through this interface's output target. */
    private void print(String message) {
        output.accept(message);
    }

    /** Displays Bambolino's welcome message. */
    public void showWelcome() {
        print(BANNER);
        print("Hello! I'm Bambolino.\n"
                + "What can I do for you?");
    }

    /** Reads the next command, treating end-of-input as a request to exit. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "bye";
    }

    /** Displays Bambolino's goodbye message. */
    public void showGoodbye() {
        print("Bye. Hope to see you again soon!");
    }

    /** Displays the task list or an empty-list message. */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            print("No tasks added yet.");
            return;
        }
        print(DIVIDER);
        print("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            print((i + 1) + "." + tasks.get(i));
        }
        print(DIVIDER);
    }

    /** Displays tasks whose descriptions contain the supplied keyword.
     *
     * @param tasks The task list to search.
     * @param keyword The case-insensitive keyword to search for.
     */
    public void showMatchingTasks(TaskList tasks, String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        print(DIVIDER);
        print("Here are the matching tasks in your list:");
        boolean hasMatches = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase().contains(lowerCaseKeyword)) {
                print((i + 1) + "." + tasks.get(i));
                hasMatches = true;
            }
        }
        if (!hasMatches) {
            print("No matching tasks found.");
        }
        print(DIVIDER);
    }

    /** Displays the confirmation for an added task. */
    public void showTaskAdded(Task task, int taskCount) {
        print(DIVIDER);
        print("Got it. I've added this task:");
        print("  " + task);
        print("Now you have " + taskCount + " tasks in the list.");
        print(DIVIDER);
    }

    /** Displays the confirmation for a marked or unmarked task. */
    public void showMarkedTask(Task task, boolean isDone) {
        print(DIVIDER);
        print(isDone ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:");
        print("  " + task);
        print(DIVIDER);
    }

    /** Displays the confirmation for a deleted task. */
    public void showDeletedTask(Task task, int taskCount) {
        print(DIVIDER);
        print("Noted. I've removed this task:");
        print("  " + task);
        print("Now you have " + taskCount + " tasks in the list.");
        print(DIVIDER);
    }

    /** Displays a user-friendly command error. */
    public void showError(String message) {
        print(DIVIDER);
        print("Sorry, " + message);
        print(DIVIDER);
    }

    /**
     * Displays a storage warning through the active interface.
     *
     * @param message The warning to display.
     */
    public void showWarning(String message) {
        print(message);
    }

    /** Displays the warning shown when saved tasks cannot be loaded. */
    public void showLoadingError() {
        print("Warning: I couldn't load your saved tasks. Starting with an empty list. "
                + "Saving is disabled to protect your existing data. "
                + "Back up and repair the data file, then restart Bambolino.");
    }
}
