package bucket;

import java.util.Scanner;

/**
 * Builds every message the user sees, and reads input when running on the console.
 *
 * Each message is returned as text rather than printed. That lets the same wording
 * serve both front ends: the console loop prints what it gets back, while the GUI
 * puts it inside a dialog box. Nothing here writes to System.out, so neither front
 * end is baked in.
 */
public class Ui {
    /** Divider drawn between console messages; the GUI uses dialog boxes instead. */
    public static final String LINE_BREAK = "-------------------------";

    private static final String BANNER = " ____   _   _   ____  _  __ _____  _____ \n"
            + "| __ ) | | | | / ___|| |/ /| ____||_   _|\n"
            + "|  _ \\ | | | || |    | ' / |  _|    | |  \n"
            + "| |_) || |_| || |___ | . \\ | |___   | |  \n"
            + "|____/  \\___/  \\____||_|\\_\\|_____|  |_|  \n";

    private Scanner scanner;

    /**
     * Opens the input stream. Call this before reading any commands.
     * Only the console front end needs this; the GUI reads from a text field.
     */
    public void start() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line the user types.
     *
     * @return Raw line, before any parsing.
     */
    public String readCommand() {
        // start() creates the scanner, and nothing else assigns it. Reaching this with
        // a null scanner means the console front end skipped a step in its lifecycle,
        // which would otherwise surface as a NullPointerException with no hint of why.
        assert scanner != null : "Ui.start() must be called before readCommand()";
        return scanner.nextLine();
    }

    /** Closes the input stream. */
    public void close() {
        assert scanner != null : "Ui.start() must be called before close()";
        scanner.close();
    }

    /**
     * Returns the ASCII-art banner.
     * Console only, since it relies on a fixed-width font to line up.
     *
     * @return Banner art.
     */
    public String getBanner() {
        return BANNER;
    }

    /**
     * Returns the greeting shown at startup.
     *
     * @return Greeting text.
     */
    public String getWelcome() {
        return "Hello! I'm Bucket\nYou again?";
    }

    /**
     * Returns the message shown while the save file is being read.
     *
     * @return Loading text.
     */
    public String getLoading() {
        return "Loading tasks ...";
    }

    /**
     * Returns the message shown when the save file held nothing.
     *
     * @return Empty-list text.
     */
    public String getNoTasks() {
        return "No tasks found.";
    }

    /**
     * Returns the whole task list, numbered from 1.
     *
     * @param items List to show.
     * @return List text, or a note that the list is empty.
     */
    public String getList(TaskList items) {
        if (items.isEmpty()) {
            return "There is nothing in your list yet.";
        }
        return "Here are the tasks in your list:\n" + items;
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param count How many tasks there are now.
     * @return Confirmation text.
     */
    public String getAdded(Task task, int count) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + count + " tasks in the list.";
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task Task that was removed.
     * @param count How many tasks are left.
     * @return Confirmation text.
     */
    public String getRemoved(Task task, int count) {
        return "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + count + " tasks in the list.";
    }

    /**
     * Returns confirmation that a task was marked done or not done.
     *
     * @param task Task that changed.
     * @param isDone True if it was marked done, false if unmarked.
     * @return Confirmation text.
     */
    public String getMarked(Task task, boolean isDone) {
        String heading = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return heading + "\n  " + task;
    }

    /**
     * Returns an error message, so every error comes out looking the same.
     *
     * @param message Text to show the user.
     * @return Error text.
     */
    public String getError(String message) {
        return message;
    }

    /**
     * Returns the tasks that matched a find, numbered from 1.
     * The numbering restarts at 1, so it does not line up with the full list.
     *
     * @param matches Tasks that matched the keyword.
     * @return Match text, or a note that nothing matched.
     */
    public String getFound(TaskList matches) {
        if (matches.isEmpty()) {
            return "No matching tasks found.";
        }
        return "Here are the matching tasks in your list:\n" + matches;
    }

    /**
     * Returns the sign-off shown when the user types bye.
     *
     * @return Goodbye text.
     */
    public String getGoodbye() {
        return "BYEEEEEEE!";
    }
}
