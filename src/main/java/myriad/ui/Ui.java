package myriad.ui;

import java.util.List;
import java.util.Scanner;

import myriad.task.Task;
import myriad.task.TaskDateTime;
import myriad.task.TaskStats;

/**
 * Owns both halves of talking to the user: every message the chatbot says,
 * including the divider lines that frame each one on the console, and the
 * reading of what the user types back. Keeping the framing here means no
 * caller has to repeat it. There is one {@code show...} method per
 * user-facing interaction.
 *
 * Every message is worded in Myriad's personality, a task painter who treats
 * the task list as a canvas and each task as a stroke on it. An error keeps
 * the plain reason written where it was detected, and only gains a painter's
 * exclamation in front, so that it still says exactly what went wrong.
 *
 * Each message is recorded into a buffer as well as (optionally) printed,
 * so the same {@code Ui} serves both front ends: the console session prints
 * as it goes, while the GUI calls {@link #startResponse()}, runs a command,
 * and reads the whole reply back with {@link #getResponse()}.
 */
public class Ui {
    /** Rule printed above and below each console message to frame it. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Painter's exclamation shown in front of every error's reason. */
    private static final String ERROR_PREFIX = "Smudge! ";

    private static final String BANNER = "███╗   ███╗██╗   ██╗██████╗ ██╗ █████╗ ██████╗ \n"
            + "████╗ ████║╚██╗ ██╔╝██╔══██╗██║██╔══██╗██╔══██╗\n"
            + "██╔████╔██║ ╚████╔╝ ██████╔╝██║███████║██║  ██║\n"
            + "██║╚██╔╝██║  ╚██╔╝  ██╔══██╗██║██╔══██║██║  ██║\n"
            + "██║ ╚═╝ ██║   ██║   ██║  ██║██║██║  ██║██████╔╝\n"
            + "╚═╝     ╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═════╝ ";

    /** Text of every message shown since the last {@link #startResponse()} call. */
    private final StringBuilder responseBuffer = new StringBuilder();

    /** Whether each message is also printed, divider-framed, to standard output. */
    private final boolean isEchoingToConsole;

    /**
     * Reader for typed input, created on first use so that a GUI session —
     * which never reads a command — does not open a {@link Scanner} on a
     * standard input stream that may not exist.
     */
    private Scanner scanner;

    /**
     * Creates a {@code Ui} that records every message so the caller can read
     * a whole reply back as one {@code String}, and additionally prints each
     * message to the console when {@code isEchoingToConsole} is true.
     *
     * @param isEchoingToConsole whether messages are also printed to standard output.
     */
    public Ui(boolean isEchoingToConsole) {
        this.isEchoingToConsole = isEchoingToConsole;
    }

    /**
     * Returns the reader for typed input, opening it on the first call.
     *
     * @return the {@code Scanner} reading standard input.
     */
    private Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }

    /**
     * Records one message as part of the reply being built, and prints it
     * between divider lines when this {@code Ui} echoes to the console.
     *
     * @param consoleOnlyText text printed above the message on the console but
     *                        left out of the recorded reply, or null when there
     *                        is none.
     * @param message         the message text, without divider lines.
     */
    private void emitWithBanner(String consoleOnlyText, String message) {
        if (responseBuffer.length() > 0) {
            responseBuffer.append('\n');
        }
        // Console messages are built with the platform line separator, but the
        // greeting's text block carries a bare newline, so a recorded reply
        // would otherwise mix the two. Normalising here keeps the recorded text
        // uniform — and keeps stray carriage returns out of a JavaFX Label —
        // without touching a single byte of what is printed below.
        responseBuffer.append(message.replace("\r\n", "\n"));

        if (isEchoingToConsole) {
            System.out.println(DIVIDER);
            if (consoleOnlyText != null) {
                System.out.println(consoleOnlyText);
            }
            System.out.println(message);
            System.out.println(DIVIDER);
        }
    }

    /**
     * Records one message, shown the same way on the console and in a GUI,
     * made of the given lines joined by the platform's line separator.
     *
     * Varargs so that a multi-line message reads as one argument per line at
     * the call site, instead of a chain of concatenated separators that is
     * easy to get wrong when a line is later added or removed. A one-line
     * message is simply a call with a single argument.
     *
     * The name is deliberately distinct from {@link #emitWithBanner}: if both
     * were called {@code emit}, the fixed two-argument form would win
     * overload resolution over this varargs one, so a two-line
     * {@code emit(a, b)} would silently print {@code a} as a console-only
     * banner instead.
     *
     * @param lines the lines of the message, in order, without divider lines.
     */
    private void emit(String... lines) {
        emitWithBanner(null, String.join(System.lineSeparator(), lines));
    }

    /**
     * Numbers every task in {@code tasks} from 1, one per line, in the format
     * shared by the {@code list}, {@code show}, {@code find} and
     * {@code stats} commands.
     *
     * @param tasks the tasks to number, in task-number order.
     * @return the numbered lines, joined by line separators.
     */
    private static String formatNumberedTasks(List<Task> tasks) {
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            lines.append(System.lineSeparator());
            lines.append(String.format("%d.%s", i + 1, tasks.get(i)));
        }
        return lines.toString();
    }

    /**
     * Returns the line that tells the user how many tasks their list now
     * holds, saying "1 task" rather than "1 tasks" when there is only one.
     *
     * @param totalCount how many tasks the list holds.
     * @return the task-count line.
     */
    private static String formatTaskCount(int totalCount) {
        String noun = (totalCount == 1) ? "task" : "tasks";
        return String.format("Your canvas now holds %d %s.", totalCount, noun);
    }

    /**
     * Discards any recorded text, so that the messages shown from now on form
     * a fresh reply.
     */
    public void startResponse() {
        responseBuffer.setLength(0);
    }

    /**
     * Returns every message shown since the last {@link #startResponse()}
     * call, as one block of text with no divider lines and {@code \n} between
     * every line,
     * whatever the platform's own line separator is.
     *
     * @return the recorded reply.
     */
    public String getResponse() {
        return responseBuffer.toString();
    }

    /**
     * Returns whether the user has typed anything more. False once the
     * input runs out, which happens when input is piped in from a file
     * rather than typed — the caller stops then, exactly as it would on
     * an explicit exit command, instead of failing on a missing line.
     *
     * @return true if another line of input is available.
     */
    public boolean hasNextCommand() {
        return getScanner().hasNextLine();
    }

    /**
     * Reads the next line the user typed, with leading and trailing
     * whitespace removed. The stripping happens here, as part of taking
     * the input in, so that everything downstream — the {@code Parser} especially
     * — can assume a tidy line.
     *
     * @return the stripped line.
     */
    public String readCommand() {
        return getScanner().nextLine().strip();
    }

    /**
     * Shows the welcome message. The ASCII banner above it is console-only:
     * its box-drawing characters only line up in a monospaced font, so a GUI
     * gets the greeting on its own.
     */
    public void showGreeting() {
        String greeting = """
                Hello! I'm Myriad, your task painter.
                What shall we paint on today's canvas?""";
        emitWithBanner(BANNER, greeting);
    }

    /**
     * Shows a farewell message.
     */
    public void showFarewell() {
        emit("Putting the brushes away. Come paint again soon!");
    }

    /**
     * Shows the standard added-task acknowledgement: the task's own
     * {@code toString()}, plus the new list size.
     *
     * @param task       the task just added.
     * @param totalCount how many tasks the list now holds.
     */
    public void showAddedTask(Task task, int totalCount) {
        emit("A fresh stroke on the canvas:",
                task.toString(),
                formatTaskCount(totalCount));
    }

    /**
     * Shows a header line followed by every task as a 1-indexed numbered list.
     *
     * @param tasks the tasks to show, in task-number order.
     */
    public void showList(List<Task> tasks) {
        emit("Here's your canvas so far:" + formatNumberedTasks(tasks));
    }

    /**
     * Shows the tasks occurring during {@code query} (from the {@code show}
     * command), 1-indexed like {@link #showList}, or a "none found" message if
     * {@code matches} is empty.
     *
     * @param matches the tasks that occur during {@code query}.
     * @param query   the date/time the user asked about, shown in the header.
     */
    public void showTasksOn(List<Task> matches, TaskDateTime query) {
        if (matches.isEmpty()) {
            emit(String.format("Nothing is painted on %s yet.", query));
        } else {
            emit(String.format("Painted on %s:", query) + formatNumberedTasks(matches));
        }
    }

    /**
     * Shows the tasks whose descriptions match {@code keyword} (from the
     * {@code find} command), 1-indexed like {@link #showList}, or a "none
     * found" message if {@code matches} is empty.
     *
     * @param matches the tasks whose descriptions contain {@code keyword}.
     * @param keyword the keyword the user searched for, shown when nothing matches.
     */
    public void showMatchingTasks(List<Task> matches, String keyword) {
        if (matches.isEmpty()) {
            emit(String.format("No strokes match \"%s\".", keyword));
        } else {
            emit("Strokes matching your search:" + formatNumberedTasks(matches));
        }
    }

    /**
     * Shows an acknowledgement that {@code task} was marked done.
     *
     * @param task the task in its new, done state.
     */
    public void showMarked(Task task) {
        emit("Beautiful! That one's painted in:", "  " + task);
    }

    /**
     * Shows an acknowledgement that {@code task} was marked not done.
     *
     * @param task the task in its new, not-done state.
     */
    public void showUnmarked(Task task) {
        emit("Back to a sketch. Marked as not done:", "  " + task);
    }

    /**
     * Shows an acknowledgement that {@code task} was removed: the task's own
     * {@code toString()}, plus the new list size.
     *
     * @param task       the task just removed.
     * @param totalCount how many tasks the list now holds.
     */
    public void showDeleted(Task task, int totalCount) {
        emit("Painted over. I've removed this task:",
                "  " + task,
                formatTaskCount(totalCount));
    }

    /**
     * Shows the {@code stats} command's report: how many tasks of each type
     * there are, which are due within the next few days, and the oldest tasks
     * not yet done — each listed 1-indexed like {@link #showList}, or a "none"
     * line if that section is empty.
     *
     * @param stats the report's data, including how many days "due soon" covers.
     */
    public void showStats(TaskStats stats) {
        String dueSoonHeader = String.format("Due in the next %d days:", stats.dueSoonDays());
        emit("Stepping back to admire your canvas:",
                String.format("Total tasks: %d (ToDo: %d, Deadline: %d, Event: %d)",
                        stats.getTotalCount(), stats.toDos().size(),
                        stats.deadlines().size(), stats.events().size()),
                stats.dueSoon().isEmpty()
                        ? dueSoonHeader + " none"
                        : dueSoonHeader + formatNumberedTasks(stats.dueSoon()),
                stats.oldestUndone().isEmpty()
                        ? "Oldest not done: none"
                        : "Oldest not done:" + formatNumberedTasks(stats.oldestUndone()));
    }

    /**
     * Shows why a command failed, after Myriad's error exclamation. The
     * reason is left plain, since it has to say exactly what went wrong.
     *
     * @param reason why the command failed, without any prefix.
     */
    public void showError(String reason) {
        emit(ERROR_PREFIX + reason);
    }

    /**
     * Shows a consolidated warning that some lines in the saved data file
     * could not be loaded and were skipped. Each entry in {@code skippedLines} is one
     * already-formatted line description; the other, valid tasks from the file
     * are unaffected and already in the task list by the time this is called.
     *
     * @param skippedLines one description per skipped line, already formatted
     *                     with its line number.
     */
    public void showLoadWarning(List<String> skippedLines) {
        StringBuilder message = new StringBuilder(String.format(
                "Warning: %d line(s) in your saved data could not be loaded and were skipped:",
                skippedLines.size()));
        for (String skipped : skippedLines) {
            message.append(System.lineSeparator());
            message.append("  - ").append(skipped);
        }
        emit(message.toString());
    }

    /**
     * Shows a warning that the saved data file couldn't be read at all (e.g.
     * permission denied). Deliberately separate from {@link #showLoadWarning}: that one
     * reports individual bad lines within a file that did load, whereas this
     * one means no task was recovered and the session starts from an empty
     * list — so the wording has to warn that saving later will overwrite
     * whatever is still in that file.
     *
     * @param message why the file couldn't be read.
     */
    public void showLoadingError(String message) {
        emit("Warning: couldn't read your saved tasks, so I'm starting with an "
                + "empty list (saving a task later will overwrite the existing data file): "
                + message);
    }
}
