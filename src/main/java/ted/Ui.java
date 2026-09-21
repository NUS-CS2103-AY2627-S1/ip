package ted;

import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

import ted.task.Task;
import ted.task.TaskList;

/**
 * Handles what Ted says to the user, and in the terminal, what the user types
 * back.
 * <p>
 * Ted's replies are worded here, and nothing else in Ted calls
 * {@code System.out} directly, so the same conversation can be shown in a
 * terminal or in a window. Error messages are the exception: they are written
 * by the code that detects the problem, since only that code knows what went
 * wrong, and reach the user through {@link #showError(String)}.
 * <p>
 * Ted speaks as a cheeky teddy bear: he teases the user a little, but every
 * reply still says plainly what happened, and every error says how to fix it.
 * <p>
 * Each {@code show} method adds to the reply being built rather than printing
 * it. {@link #flush()} hands the finished reply back, so the terminal can print
 * it while the GUI puts it in a dialog box.
 */
public class Ui {
    /** Horizontal rule framing each of Ted's replies. */
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Times of day Ted has a soft spot for, like the teddy bear in the film he
     * shares a name with. A task set for one of them earns a remark.
     */
    private static final List<LocalTime> FAVORITE_TIMES = List.of(LocalTime.of(4, 20), LocalTime.of(16, 20));

    /** From this many tasks on, Ted suggests finishing some before adding more. */
    private static final int BUSY_TASK_COUNT = 10;

    /** Reads the user's commands from standard input, one line at a time. */
    private final Scanner scanner = new Scanner(System.in);

    /** The reply being built, emptied by each call to {@link #flush()}. */
    private final StringBuilder reply = new StringBuilder();

    /** Creates a Ui with nothing said yet. */
    public Ui() {
    }

    /**
     * Returns whether the user has typed another line.
     * Guards against the input stream ending (e.g. Ctrl-D or a piped file),
     * which would otherwise make {@link #readCommand()} throw.
     *
     * @return {@code true} if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command typed by the user.
     *
     * @return the line the user typed, with surrounding spaces removed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Returns everything said since the previous call and starts a fresh reply.
     *
     * @return the finished reply, without a trailing newline.
     */
    public String flush() {
        // Only trailing space is removed, so that a reply starting with an
        // indented line keeps its indent.
        String finishedReply = reply.toString().stripTrailing();
        reply.setLength(0);
        return finishedReply;
    }

    /**
     * Prints a finished reply to the terminal, framed by horizontal rules.
     * Used by the text interface only; the GUI shows the same text in a
     * dialog box, where the rules would be clutter.
     *
     * @param finishedReply the reply to print, as returned by {@link #flush()}.
     */
    public void printReply(String finishedReply) {
        System.out.println(DIVIDER);
        System.out.println(finishedReply);
        System.out.println(DIVIDER);
    }

    /** Greets the user at startup. */
    public void showWelcome() {
        show("Oh good, you're here. I'm Ted: professional teddy bear, part-time life coach.",
                "What do you need to get done today?");
    }

    /** Says goodbye just before Ted stops. */
    public void showGoodbye() {
        show("Leaving already? Fine. I'll be here on the shelf. Bye!");
    }

    /**
     * Reports a problem Ted can recover from.
     *
     * @param message explanation of what went wrong, phrased as Ted would say it.
     */
    public void showError(String message) {
        show(message);
    }

    /**
     * Warns that the saved tasks could not be read, so Ted starts empty.
     *
     * @param message explanation of what went wrong, phrased as Ted would say it.
     */
    public void showLoadingError(String message) {
        show(message, "I'll start you on an empty list for now.");
    }

    /**
     * Warns that some lines of the save file were not in the expected format.
     *
     * @param skippedLineCount how many lines were skipped.
     */
    public void showSkippedLines(int skippedLineCount) {
        String lineWord = skippedLineCount == 1 ? "line" : "lines";
        show("Some of your save file made no sense to me, so I skipped " + skippedLineCount + " "
                + lineWord + ".");
    }

    /**
     * Tells the user where the unreadable save file was copied, so that
     * nothing in it is lost when Ted next saves.
     *
     * @param backupFile where the copy was made.
     */
    public void showBackup(Path backupFile) {
        show("Don't worry, I kept a copy of the original file at " + backupFile + ".");
    }

    /**
     * Confirms a newly added task, with a remark if it is set for one of
     * Ted's favorite times.
     *
     * @param task      the task that was added.
     * @param taskCount how many tasks are now stored.
     */
    public void showAdded(Task task, int taskCount) {
        show("Look at you, being responsible. I've added:",
                "  " + task);
        if (FAVORITE_TIMES.stream().anyMatch(task::isAtTimeOfDay)) {
            show("4:20, huh? Excellent choice of time.");
        }
        showTaskCount(taskCount);
    }

    /**
     * Confirms a deleted task. The task is echoed because once it is gone the
     * user has no other way to check that the number they typed was the one
     * they meant.
     *
     * @param task      the task that was removed.
     * @param taskCount how many tasks are left.
     */
    public void showRemoved(Task task, int taskCount) {
        show("Gone, just like that. I've removed:",
                "  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms a change to a task's done status.
     *
     * @param task   the task whose status changed.
     * @param isDone {@code true} if the task was marked done, {@code false} if reversed.
     */
    public void showMarked(Task task, boolean isDone) {
        show(isDone
                        ? "You actually did it? I'm impressed. Marked as done:"
                        : "Changed your mind? Marked as not done:",
                "  " + task);
    }

    /**
     * Confirms a change to a task's tags.
     *
     * @param task     the task whose tags changed.
     * @param isAdding {@code true} if tags were attached, {@code false} if detached.
     */
    public void showTagged(Task task, boolean isAdding) {
        show(isAdding ? "Fancy. I've tagged it:" : "Tag removed. Here it is now:",
                "  " + task);
    }

    /**
     * Shows every stored task as a numbered list, starting from 1.
     *
     * @param tasks the tasks to show.
     */
    public void showTasks(TaskList tasks) {
        showNumbered(tasks, tasks, "Here's everything you said you'd do:",
                "Your list is empty. Either you're all done, or you haven't started yet.");
    }

    /**
     * Shows the tasks that matched a search, each with its number in the full
     * list, so that the number shown is the one to pass to mark, delete or tag.
     *
     * @param tasks   every stored task, which the numbers refer to.
     * @param matches the tasks that matched.
     * @param keyword what the user searched for, repeated back when nothing matched.
     */
    public void showMatchingTasks(TaskList tasks, TaskList matches, String keyword) {
        showNumbered(tasks, matches, "Here's what I found:",
                "Nothing matches \"" + keyword + "\". I even checked under the sofa. "
                        + "Found some snacks, though.");
    }

    /**
     * Adds tasks to the reply as a list, each numbered by its place in the
     * full task list.
     *
     * @param tasks        every stored task, which the numbers refer to.
     * @param shownTasks   the tasks to show, all taken from {@code tasks}.
     * @param header       line introducing the list.
     * @param emptyMessage line to show instead when there are no tasks to show.
     */
    private void showNumbered(TaskList tasks, TaskList shownTasks, String header, String emptyMessage) {
        if (shownTasks.isEmpty()) {
            show(emptyMessage);
            return;
        }

        show(header);
        for (Task task : shownTasks.asList()) {
            show(tasks.getNumberOf(task) + "." + task);
        }
    }

    /**
     * Adds lines to the reply being built.
     * Most replies are a fixed handful of lines, so taking them as varargs lets
     * each one be written as a single call that reads like the reply itself.
     *
     * @param lines the lines to add, in order.
     */
    private void show(String... lines) {
        for (String line : lines) {
            reply.append(line).append(System.lineSeparator());
        }
    }

    /**
     * Tells the user how many tasks are now stored, with a remark that suits
     * the size of the list.
     *
     * @param taskCount how many tasks are now stored.
     */
    private void showTaskCount(int taskCount) {
        if (taskCount == 0) {
            show("Your list is empty now. Enjoy it while it lasts.");
        } else if (taskCount == 1) {
            show("That's 1 task. A small start, but a start.");
        } else if (taskCount < BUSY_TASK_COUNT) {
            show("That's " + taskCount + " tasks. Still manageable.");
        } else {
            show("That's " + taskCount + " tasks. Maybe finish a few before adding more?");
        }
    }
}
