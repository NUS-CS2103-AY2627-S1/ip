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

    /** Prefix on lines spoken by Bibi. */
    private static final String SPEAKER = "Bibi: ";

    private final Scanner scanner;

    /** Lines collected while capturing, in the order they were written. */
    private final StringBuilder capturedText = new StringBuilder();

    /** Whether output is being collected rather than printed. */
    private boolean isCapturing;

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
        capturedText.setLength(0);
    }

    /**
     * Stops collecting and returns everything written since {@link #startCapture()}.
     *
     * @return the collected lines, separated by newlines and trimmed of blank ends
     */
    public String takeCapturedText() {
        isCapturing = false;
        return capturedText.toString().strip();
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
        write(isCapturing ? message : SPEAKER + message);
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
    public void showDetail(String... details) {
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
     * Prints the greeting and a summary of the supported commands.
     */
    public void showWelcome() {
        showMessage("Enter todo <description>, deadline <description> /by <time>,");
        showPlain("or event <description> /from <start> /to <end>.");
        showMessage("Type list, find <keyword>, on <date>, mark <number>, or bye.");
        showMessage("Dates look like 2019-10-15 or 2/12/2019 1800.");
    }

    /**
     * Prints the full list of commands Bibi understands.
     */
    public void showHelp() {
        showMessage("Here are the commands I understand:");
        showDetail(
                "todo <description>",
                "deadline <description> /by <time>",
                "event <description> /from <start> /to <end>",
                "list",
                "find <keyword>",
                "on <date>",
                "mark <number>",
                "unmark <number>",
                "remove <number>",
                "help",
                "bye");
    }

    /**
     * Prints the parting message.
     */
    public void showGoodbye() {
        showMessage("Goodbye! Till next time...");
    }

    /**
     * Reports a problem with the user's command.
     *
     * @param message the explanation to show
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Reports how many tasks were restored from the save file.
     *
     * @param taskCount the number of tasks that were loaded
     */
    public void showLoaded(int taskCount) {
        showMessage("Loaded " + taskCount + " saved task(s).");
    }

    /**
     * Reports the save-file lines that could not be understood.
     *
     * @param warnings one explanation per skipped line
     */
    public void showLoadWarnings(List<String> warnings) {
        showMessage("I had trouble reading part of your save file:");
        for (String warning : warnings) {
            showDetail(warning);
        }
        showMessage("Those entries are skipped, and will be dropped from the file the next time "
                + "your task list changes.");
    }

    /**
     * Reports that the save file could not be read at all.
     *
     * @param filePath the file that could not be read
     * @param exception the error that stopped it being read
     */
    public void showLoadingError(Path filePath, IOException exception) {
        showMessage("I could not read your saved tasks from " + filePath + " ("
                + describe(exception) + "). Starting with an empty list.");
    }

    /**
     * Reports that the task list could not be written to disk.
     *
     * @param filePath the file that could not be written
     * @param exception the error that stopped it being written
     */
    public void showSaveError(Path filePath, IOException exception) {
        showMessage("I could not save your tasks to " + filePath + " (" + describe(exception)
                + "). Changes made in this session will be lost when Bibi closes.");
    }

    /**
     * Describes a file error in a form short enough to show in the console.
     *
     * @param exception the file error to describe
     * @return the error type followed by any detail it carries
     */
    private static String describe(IOException exception) {
        return exception.getClass().getSimpleName() + ": " + exception.getMessage();
    }
}
