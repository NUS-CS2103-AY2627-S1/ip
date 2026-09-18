package benjamin.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import benjamin.task.Task;
import benjamin.task.TaskDateTime;
import benjamin.task.TaskList;

/**
 * Deals with everything the user sees and types.
 *
 * <p>Replies are collected into a buffer rather than printed straight away.
 * The text interface empties that buffer to the console after each command,
 * while the graphical interface takes the same text and puts it in a dialog
 * box. Both interfaces therefore show exactly the same wording.
 *
 * <p>All of Benjamin's wording lives in this class, which is what gives him a
 * consistent voice: unimpressed, faintly weary, and never actually unhelpful.
 * Problem messages stay plainly factual on purpose, since a joke the user
 * cannot act on is worse than no joke at all.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private static final String GREETING = "Benjamin. I hold your list so you don't have to.\nGo on, then.";

    private static final String FAREWELL = "Off you go. The list will keep.";

    /** Marks a reply as a complaint, in the text interface. */
    private static final String PROBLEM_PREFIX = "Hm. ";

    private static final String BANNER = " ____             _                 _\n"
            + "| __ )  ___ _ __ (_) __ _ _ __ ___ (_)_ __\n"
            + "|  _ \\ / _ \\ '_ \\| |/ _` | '_ ` _ \\| | '_ \\\n"
            + "| |_) |  __/ | | | | (_| | | | | | | | | | |\n"
            + "|____/ \\___|_| |_|/ |\\__,_|_| |_| |_|_|_| |_|\n"
            + "                |__/\n";

    private final StringBuilder buffer = new StringBuilder();

    /** Whether the reply being built reported a problem. */
    private boolean hadProblem;
    private final Scanner scanner;

    /** Creates a user interface that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Returns true while the user still has a line of input to give. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Returns the next command typed by the user, without surrounding spaces. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Returns everything shown since the last call and empties the buffer.
     *
     * @return the collected reply, which is empty if nothing was shown.
     */
    public String flush() {
        String reply = buffer.toString();
        buffer.setLength(0);
        hadProblem = false;
        // A leftover buffer would prepend this reply to the next one, which in
        // the GUI shows up as an answer to the wrong question.
        assert buffer.isEmpty() : "buffer must be empty after a flush";

        return reply;
    }

    /** Writes everything shown since the last call to the console. */
    public void flushToConsole() {
        System.out.print(flush());
    }

    /** Shows the divider line used to separate replies in the text interface. */
    public void showLine() {
        print(DIVIDER);
    }

    /** Shows the banner and the opening greeting, for the text interface. */
    public void showWelcome() {
        print(BANNER + GREETING + "\n" + DIVIDER);
    }

    /** Shows the opening greeting without the banner, for the graphical interface. */
    public void showGreeting() {
        print(GREETING);
    }

    /** Shows the sign off line. The surrounding dividers are added by the caller. */
    public void showFarewell() {
        print(FAREWELL);
    }

    /**
     * Reports a problem to the user.
     *
     * @param message the wording to show, which is prefixed here so that every
     *     problem looks the same.
     */
    public void showError(String message) {
        hadProblem = true;
        print(PROBLEM_PREFIX + message);
    }

    /**
     * Returns true if the reply being built reported a problem.
     *
     * <p>The graphical interface uses this to show complaints in their own
     * style, so a mistake is not mistaken for an ordinary answer. Reading it
     * has to happen before {@link #flush()}, which clears it.
     */
    public boolean hadProblem() {
        return hadProblem;
    }

    /** Reports that the save file could not be read at all. */
    public void showLoadingError() {
        showError("I couldn't read your saved list, so we begin with nothing. "
                + "A clean slate, if you want to look on the bright side.");
    }

    /**
     * Shows every task, numbered from one.
     *
     * @param tasks the list to show.
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            print("Your list is empty. Suspicious, but not my problem.");
            return;
        }

        print("Here's what you've signed up for:");
        showNumbered(tasks.asList());
    }

    /**
     * Reports the tasks that fall on the given date, or says there are none.
     *
     * @param date the day that was asked about.
     * @param matches the tasks falling on that day, possibly empty.
     */
    public void showTasksOn(LocalDate date, List<Task> matches) {
        if (matches.isEmpty()) {
            print("Nothing on " + TaskDateTime.formatDate(date) + ". Enjoy it while it lasts.");
            return;
        }

        print("On " + TaskDateTime.formatDate(date) + ", you have:");
        showNumbered(matches);
    }

    /**
     * Reports the tasks matching a search, or says that none do.
     *
     * @param matches the tasks whose description or tags contained the keyword.
     */
    public void showMatchingTasks(List<Task> matches) {
        if (matches.isEmpty()) {
            print("Nothing matches. Either it's done, or you imagined it.");
            return;
        }

        print("These match, for what it's worth:");
        showNumbered(matches);
    }

    /**
     * Confirms that a task was added.
     *
     * @param task the task just added.
     * @param taskCount how many tasks there are now.
     */
    public void showAdded(Task task, int taskCount) {
        print("Fine. Added:", "  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms that a task was removed.
     *
     * @param task the task just removed.
     * @param taskCount how many tasks are left.
     */
    public void showRemoved(Task task, int taskCount) {
        print("Gone. I won't ask why:", "  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task in its new state.
     */
    public void showMarked(Task task) {
        print("Done, apparently:", "  " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task in its new state.
     */
    public void showUnmarked(Task task) {
        print("Back on the pile:", "  " + task);
    }

    /**
     * Confirms that a label was attached.
     *
     * @param task the task with its new tag already shown.
     */
    public void showTagged(Task task) {
        print("Tagged. Riveting:", "  " + task);
    }

    /**
     * Confirms that a label was removed.
     *
     * @param task the task without that tag.
     */
    public void showUntagged(Task task) {
        print("Tag removed:", "  " + task);
    }

    /**
     * Reports that the task already carried the label, so nothing changed.
     *
     * @param tag the label that was already there.
     */
    public void showAlreadyTagged(String tag) {
        print("It's already tagged #" + tag + ". Do try to keep up.");
    }

    /**
     * Reports that the task did not carry the label, so nothing changed.
     *
     * @param tag the label that was not found.
     */
    public void showNotTagged(String tag) {
        print("It was never tagged #" + tag + ".");
    }

    /** Stops reading input. */
    public void close() {
        scanner.close();
    }

    private void showNumbered(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            print(String.format("%d.%s", i + 1, tasks.get(i)));
        }
    }

    private void showTaskCount(int taskCount) {
        String noun = taskCount == 1 ? "thing" : "things";
        print("That's " + taskCount + " " + noun + " on the list.");
    }

    /**
     * Adds one line per argument to the reply being built.
     *
     * <p>Taking a varargs list lets the callers that always show a fixed group
     * of lines say so in a single call.
     *
     * @param lines the lines to show, in order.
     */
    private void print(String... lines) {
        for (String line : lines) {
            buffer.append(line).append(System.lineSeparator());
        }
    }
}
