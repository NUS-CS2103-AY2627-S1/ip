package suki;

import java.util.List;
import java.util.Scanner;

/**
 * Handles everything the user sees and types.
 *
 * <p>Keeping all reading and printing in one class means the rest of Suki can
 * be tested and changed without worrying about console formatting, and a
 * different front end (a GUI, say) could replace this class alone.
 */
public class Ui {
    /** The ASCII-art logo shown at startup. */
    private static final String BANNER =
            " ____   _   _  _  __ ___ \n"
                    + "/ ___| | | | || |/ /|_ _|\n"
                    + "\\___ \\ | | | || ' /  | | \n"
                    + " ___) || |_| || . \\  | | \n"
                    + "|____/  \\___/ |_|\\_\\|___|\n";
    /** The horizontal rule separating Suki's replies. */
    private static final String LINE =
            "____________________________________________________________";

    /** Reads the user's typed input. */
    private final Scanner scanner;

    /**
     * Everything shown since {@link #takeOutput()} was last called.
     *
     * <p>The console prints as it goes, but the GUI needs a whole reply as one
     * string to put in a dialog box. Recording the lines here lets both share
     * the same message-building code instead of each formatting its own.
     */
    private final StringBuilder captured = new StringBuilder();

    /** Creates a Ui that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether the user has entered another line of input.
     *
     * @return true if there is more input to read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next line the user typed.
     *
     * @return the line, without its trailing newline
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Stops reading input. Call this once, when the program is finishing. */
    public void close() {
        scanner.close();
    }

    /**
     * Prints each of the given lines on a row of its own.
     *
     * <p>This takes varargs because Suki's replies are fixed sequences of
     * lines whose length differs from one message to the next: an error is
     * one line, a confirmation is two or three, and the greeting is five.
     * Varargs lets each caller pass exactly the lines it has, without
     * every one of them having to build a list or repeat a print loop.
     *
     * @param lines the lines to print, in order
     */
    public void show(String... lines) {
        for (String line : lines) {
            System.out.println(line);
            captured.append(line).append(System.lineSeparator());
        }
    }

    /**
     * Returns everything shown since this method was last called, and forgets
     * it so that the next reply starts empty.
     *
     * <p>Only the GUI uses this; the console has already printed the lines by
     * the time this returns them.
     *
     * @return the lines shown since the last call, as one string
     */
    public String takeOutput() {
        String output = captured.toString().strip();
        captured.setLength(0);
        return output;
    }

    /** Prints the horizontal rule separating Suki's replies. */
    public void showLine() {
        show(LINE);
    }

    /** Prints the banner and greeting shown when Suki starts. */
    public void showWelcome() {
        show(LINE, BANNER);
        showGreeting();
        show(LINE);
    }

    /**
     * Prints the greeting on its own, without the banner and horizontal rules
     * that only make sense in a console.
     */
    public void showGreeting() {
        show("Hello! I'm Suki.", "What can I do for you?");
    }

    /** Prints the parting message shown when the user leaves. */
    public void showGoodbye() {
        showFarewell();
        show(LINE);
    }

    /**
     * Prints the parting message on its own, without the horizontal rule that
     * only makes sense in a console.
     */
    public void showFarewell() {
        show("Bye. Hope to see you again soon!");
    }

    /**
     * Prints an error in the standard format.
     *
     * @param message the explanation to show the user
     */
    public void showError(String message) {
        show("OOPS!!! " + message);
    }

    /**
     * Prints the tasks, numbered from 1, or a note if there are none.
     *
     * @param tasks the tasks to show
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            show("Your list is empty.");
            return;
        }
        show("Here are the tasks in your list:");
        showNumbered(tasks);
    }

    /**
     * Confirms that the list was sorted, and shows the new order.
     *
     * <p>The whole list is shown because the point of sorting is the order,
     * which a one-line confirmation would not convey.
     *
     * @param tasks the tasks, already sorted
     */
    public void showTasksSorted(TaskList tasks) {
        if (tasks.isEmpty()) {
            show("There is nothing to sort. Your list is empty.");
            return;
        }
        show("Sorted. Here are your tasks, earliest first:");
        showNumbered(tasks);
    }

    /**
     * Prints the tasks matching a search, or a note if there were none.
     *
     * @param matches the tasks that matched
     */
    public void showFoundTasks(TaskList matches) {
        if (matches.isEmpty()) {
            show("No matching tasks found.");
            return;
        }
        show("Here are the matching tasks in your list:");
        showNumbered(matches);
    }

    /**
     * Prints the given tasks one per line, numbered from 1.
     *
     * <p>Shared by the full listing and the search results, which differ only
     * in the heading above them.
     *
     * <p>This reads the list directly rather than calling {@link TaskList#get},
     * whose bounds check throws a checked exception that cannot happen for
     * indices generated by this loop.
     *
     * @param tasks the tasks to print, in the order they should appear
     */
    private void showNumbered(TaskList tasks) {
        List<Task> list = tasks.asList();
        for (int i = 0; i < list.size(); i++) {
            show((i + 1) + "." + list.get(i));
        }
    }

    /**
     * Confirms that a task was added.
     *
     * @param task the task that was added
     * @param taskCount how many tasks there are now
     */
    public void showTaskAdded(Task task, int taskCount) {
        show("Got it. I've added this task:", "  " + task, taskCountMessage(taskCount));
    }

    /**
     * Confirms that a task was removed.
     *
     * @param task the task that was removed
     * @param taskCount how many tasks there are now
     */
    public void showTaskRemoved(Task task, int taskCount) {
        show("Noted. I've removed this task:", "  " + task, taskCountMessage(taskCount));
    }

    /**
     * Confirms that a task is now done.
     *
     * @param task the task that was marked
     */
    public void showTaskMarked(Task task) {
        show("Nice! I've marked this task as done:", "  " + task);
    }

    /**
     * Confirms that a task is no longer done.
     *
     * @param task the task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        show("OK, I've marked this task as not done yet:", "  " + task);
    }

    /**
     * Returns the line reporting how many tasks remain, shared by the add
     * and remove confirmations.
     *
     * <p>This returns the line rather than printing it so that callers can
     * hand it to {@link #show(String...)} alongside their other lines,
     * keeping each message a single call.
     *
     * @param taskCount how many tasks there are now
     * @return the line to show the user
     */
    private String taskCountMessage(int taskCount) {
        assert taskCount >= 0 : "a task count is a list size and cannot be negative";
        return "Now you have " + taskCount + " tasks in the list.";
    }
}
