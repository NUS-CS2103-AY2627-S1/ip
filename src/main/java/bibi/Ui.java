package bibi;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import bibi.task.Task;

/**
 * Handles everything Bibi shows to the user and reads back from them.
 *
 * <p>This class owns the mechanics of the conversation: the prompt, the divider
 * lines, the {@code Bibi:} prefix, and the numbering of listed tasks. Wording
 * that belongs to one particular operation is passed in by the caller, so that
 * adding a command does not mean adding a method here.
 *
 * <p>Every line leaves through {@link #write(String)}, which either prints to the
 * console or collects the line in memory. Collecting is what lets the GUI reuse
 * the commands unchanged: it asks for the same work to be done, then takes the
 * text that would have been printed and puts it in a dialog box instead.
 *
 * <p>Keeping all console access in one place means the rest of the program never
 * calls {@code System.out} directly, which is what makes the other classes
 * testable without capturing console output.
 */
public class Ui {
    /** Divider printed around a block of output. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Prefix on each line Bibi speaks in the console. */
    private static final String SPEAKER_PREFIX = "Bibi: ";

    private final Scanner scanner;

    /** Lines collected while capturing, in the order they were written. */
    private final StringBuilder capturedText = new StringBuilder();

    /** Whether output is being collected rather than printed. */
    private boolean isCapturing;

    /** Whether anything captured so far was reported as a problem. */
    private boolean isErrorCaptured;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Starts collecting output instead of printing it, discarding anything held
     * from an earlier collection.
     *
     * <p>Used by the GUI, which needs the words as a string rather than on the
     * console.
     */
    public void startCapture() {
        isCapturing = true;
        isErrorCaptured = false;
        capturedText.setLength(0);
    }

    /**
     * Stops collecting and returns everything written since {@link #startCapture()}.
     *
     * @return the collected lines, separated by newlines and trimmed of blank ends
     */
    public Reply takeCapturedReply() {
        // Taking text without starting a capture would silently return whatever
        // the previous capture left behind, so the GUI would show a stale reply
        // rather than fail.
        assert isCapturing : "takeCapturedReply called without a matching startCapture";

        isCapturing = false;

        // Blank lines at either end are trimmed, but not the leading spaces of
        // the first real line: showDetails indents its lines, and a reply that
        // opens with one would otherwise lose that indent.
        String text = capturedText.toString().stripTrailing();
        while (text.startsWith("\n")) {
            text = text.substring(1);
        }
        return new Reply(text, isErrorCaptured);
    }

    /**
     * Sends one line to wherever output is currently going.
     *
     * <p>A newline is used rather than the platform separator because captured
     * text is destined for a JavaFX label, which only breaks lines on {@code \n}.
     *
     * @param line the line to show, already formatted
     */
    private void write(String line) {
        assert line != null : "a null line would be printed as the text \"null\"";

        if (isCapturing) {
            capturedText.append(line).append('\n');
        } else {
            System.out.println(line);
        }
    }

    /**
     * Prompts for and returns the user's next command.
     *
     * <p>Reaching the end of the input counts as saying goodbye, which keeps the
     * program from failing when its input is piped in from a file.
     *
     * @return the command with surrounding spaces removed
     */
    public String readCommand() {
        System.out.print("You: ");
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException endOfInput) {
            return "bye";
        }
    }

    /**
     * Releases the input source when the conversation is over.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Prints the divider line used to separate blocks of output.
     */
    public void showLine() {
        write(DIVIDER);
    }

    /**
     * Prints one line spoken by Bibi.
     *
     * @param message the words to show, without the speaker prefix
     */
    public void showMessage(String message) {
        // The GUI puts Bibi's picture beside every reply, so the prefix that names
        // the speaker in the console would only be noise repeated down the window.
        write(isCapturing ? message : SPEAKER_PREFIX + message);
    }

    /**
     * Prints supporting lines that continue the message above them.
     *
     * <p>Takes varargs because the callers with several of these, {@link #showHelp()}
     * above all, have them as a fixed list written out in place. One call listing
     * them reads better than a run of near-identical calls, and the caller that
     * has only one line to show is unaffected.
     *
     * @param details the lines to indent, shown in the order given
     */
    public void showDetails(String... details) {
        for (String detail : details) {
            write("  " + detail);
        }
    }

    /**
     * Prints a line exactly as given, without a prefix or indent.
     *
     * @param text the text to show
     */
    public void showPlain(String text) {
        write(text);
    }

    /**
     * Prints one task with the number the user would refer to it by.
     *
     * @param taskNumber the one-based number of the task
     * @param task the task to show
     */
    public void showNumberedTask(int taskNumber, Task task) {
        write(taskNumber + ". " + task);
    }

    /**
     * Prints the opening banner.
     *
     * <p>Kept apart from {@link #showWelcome()} because the letters are drawn out
     * of spaced characters and only line up in the console's fixed-width font.
     * The GUI shows the welcome without it.
     */
    public void showBanner() {
        String banner = "B B B B    i    b b b    i\n"
                + "B       B       b       b\n"
                + "B B B B   iii   b b b b  iii\n"
                + "B       B  i    b       b  i\n"
                + "B B B B  iii   b b b b  iii\n";
        write(banner);
    }

    /**
     * Prints the greeting and points the user at the commands.
     *
     * <p>Only the commands that add tasks are named here. The full list is
     * written out in {@link #showHelp()} alone, so adding a command means
     * updating one list rather than several.
     */
    public void showWelcome() {
        showMessage("I'm Bibi. Alright, what did you wake me up for?");
        showMessage("Add work with todo, deadline or event.");
        showMessage("Type help to see everything else I can do.");
        showMessage("Dates go day first: 21/12/2026 or 21 Dec 2026 6 pm.");
    }

    /**
     * Prints the full list of commands Bibi understands, one to a line.
     *
     * <p>This is the only place the full list is written out; the greeting and
     * the reply to an unknown command both point here instead.
     *
     * <p>The synopses are padded into a column so the reply scans like the help
     * of a command-line tool: the reader looks down the left edge for the
     * command and across only once they have found it. Everything that is not a
     * command, such as the date formats and the keyboard shortcuts, is held back
     * for {@link #showHelpExamples()} so that this list stays short enough to
     * take in at a glance.
     */
    public void showHelp() {
        showMessage("Usage: <command> [arguments]");
        showDetails(
                "Add",
                "  todo <description>                           add an undated task",
                "  deadline <description> /by <time>            add a task with a due date",
                "  event <description> /from <start> /to <end>  add a task that spans two times",
                "View",
                "  list                                         show every task with its number",
                "  sort                                         save tasks in date order, undated last",
                "  find <keyword>                               show tasks whose description matches",
                "  on <date>                                    show deadlines and events on one day",
                "Update",
                "  mark <number>                                mark a task done",
                "  unmark <number>                              reopen a task",
                "  remove|delete <number>                       delete a task",
                "  undo                                         reverse the last task change",
                "Session",
                "  help [--examples]                            show this reference",
                "  bye                                          end the session",
                "  hi, hello, hey, thanks                       a quick reply");
        showMessage("Run help --examples for examples, date formats and keyboard shortcuts.");
    }

    /**
     * Prints worked examples and the reference detail behind the command list.
     *
     * <p>Kept behind a flag rather than shown with {@link #showHelp()} because
     * the two answer different questions. Someone who has forgotten a command
     * word wants the short list; someone who cannot get a date accepted wants
     * this. Printing both every time made the common case scroll past the
     * uncommon one.
     */
    public void showHelpExamples() {
        showMessage("Examples:");
        showDetails(
                "todo read book",
                "deadline return book /by 21/12/2026 1800",
                "event study group /from 21/12/2026 1800 /to 21/12/2026 2000",
                "find book",
                "on 21/12/2026",
                "mark 2");
        showMessage("Dates:");
        showDetails(
                "d/M/yyyy, d-M-yyyy, d.M.yyyy, d MMM yyyy or d MMMM yyyy.",
                "d/M/yy uses 00-99 for 2000-2099. Numeric dates are always day first.",
                "Existing yyyy-MM-dd input still works. Examples: 1/2/2026, 21 December 2026, 21/12/26.",
                "Optional time: 1800, 18:00, 6 pm or 6:00 pm. Display: 21 Dec 2026 6:00PM.");
        showMessage("Task numbers:");
        showDetails(
                "Numbers come from the full list, including find/on results. Sort and remove/delete change them;",
                "use list for current numbers before acting on an older reply.",
                "undo restores the last task change once this session; there is no redo.");
        showMessage("Keyboard, in the window:");
        showDetails(
                "Enter sends; Up/Down recall commands without sending. Down past the newest restores your draft.",
                "Rejected commands stay for editing. Select transcript text and use Ctrl+C (Cmd+C on macOS) to copy.",
                "New replies scroll into view when you're near the bottom; scroll up to keep your reading position.");
    }

    /**
     * Prints the parting message.
     */
    public void showGoodbye() {
        showMessage("See you. Your saved tasks will be here next time.");
    }

    /**
     * Reports something the user needs to notice and act on.
     *
     * <p>Besides showing the message, this flags the reply as an error, so the
     * GUI can style it apart from an ordinary confirmation. Rejected commands
     * and failed reads and writes of the save file all come through here.
     *
     * @param message the explanation to show
     */
    public void showError(String message) {
        isErrorCaptured = true;
        showMessage(message);
    }

    /**
     * Reports how many tasks were restored from the save file.
     *
     * @param taskCount the number of tasks that were loaded
     */
    public void showLoaded(int taskCount) {
        showMessage("Picked up where we left off: " + describeCount(taskCount) + " restored.");
    }

    /**
     * Reports the save-file lines that could not be understood.
     *
     * @param warnings one explanation per skipped line
     */
    public void showLoadWarnings(List<String> warnings) {
        showError("I couldn't read these lines in the save file:");
        for (String warning : warnings) {
            showDetails(warning);
        }
        showMessage("I've skipped those lines. They will leave the file the next time "
                + "your list changes.");
    }

    /**
     * Reports that the save file could not be read at all.
     *
     * @param filePath the file that could not be read
     * @param exception the error that stopped it being read
     */
    public void showLoadError(Path filePath, IOException exception) {
        showError("I couldn't read your saved tasks from " + filePath + " ("
                + describeException(exception) + "). Starting with an empty list.");
    }

    /**
     * Reports that the task list could not be written to disk.
     *
     * <p>Flagged as an error even though the command itself succeeded, because
     * the user stands to lose this session's changes and must not mistake the
     * reply for a routine confirmation.
     *
     * @param filePath the file that could not be written
     * @param exception the error that stopped it being written
     */
    public void showSaveError(Path filePath, IOException exception) {
        showError("I couldn't save your tasks to " + filePath + " (" + describeException(exception)
                + "). The change is applied in memory but isn't saved. Don't repeat it. "
                + "Keep Bibi open and fix the file problem; "
                + "changes may be lost if you close before a successful save.");
    }

    /**
     * Describes a number of tasks with the right plural.
     *
     * <p>Exists because "1 tasks" is the kind of small wrongness that makes an
     * app feel unfinished, and the count is shown from several places.
     *
     * @param taskCount how many tasks are being described
     * @return the count followed by "task" or "tasks"
     */
    public static String describeCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

    /**
     * Describes a file error in a form short enough to show in the console.
     *
     * @param exception the file error to describe
     * @return the error type followed by any detail it carries
     */
    private static String describeException(IOException exception) {
        return exception.getClass().getSimpleName() + ": " + exception.getMessage();
    }
}
