package myriad;

import java.io.File;
import java.util.List;

import myriad.command.Command;
import myriad.parser.Parser;
import myriad.storage.LoadResult;
import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.ui.Ui;

/**
 * Entry point for the Myriad chatbot.
 * Loads any saved tasks from disk, greets the user, then reads lines of
 * input, each treated as a command (adding, listing, marking, deleting and
 * searching tasks), until the user types {@code bye} or the input runs out,
 * then says goodbye. A line that doesn't match any known command, or that's
 * missing a required argument, throws a {@link MyriadException}, which is
 * caught once per line in {@link #executeLine} and shown as an error.
 *
 * One chatbot session is one {@code Myriad} object: it holds the pieces
 * that session needs — the {@link Ui} it talks through, the {@link Storage}
 * it saves to, and the {@link TaskList} it works on — and hands all three to
 * each {@link Command} it runs. Working out what a line means is the
 * {@link Parser}'s job and carrying it out is the {@code Command}'s, so this
 * class is left with the wiring: set the three up, then feed lines through
 * them.
 */
public class Myriad {

    /**
     * Path of the data file both front ends load from and save to, so the
     * console and the GUI share one saved task list. Built with {@link File}
     * rather than a {@code "data/myriad.txt"} literal so the separator is
     * right on every OS.
     */
    public static final String DEFAULT_DATA_FILE = new File("data", "myriad.txt").getPath();

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Descriptions of any saved-data lines that couldn't be loaded; empty if
     * every line loaded or the whole file couldn't be read. Held from
     * construction until the greeting has been shown, because a load problem
     * is reported after the greeting rather than before it.
     */
    private final List<String> skippedLines;

    /** Why the whole data file couldn't be read, or null if it was read. */
    private final String loadErrorMessage;

    /**
     * Whether a command has asked for the session to end. Only a GUI needs
     * this: the console loop learns the same thing from
     * {@link Command#isExit()}, but a GUI only ever sees the reply text, so
     * the request is recorded here for it to act on.
     */
    private boolean isExitRequested = false;

    /**
     * Whether the most recent reply from {@link #getResponse} reported an
     * error. Like {@link #isExitRequested}, this exists for a GUI: the reply
     * is plain text, so without it a GUI could not tell an error apart from
     * an ordinary answer in order to highlight it.
     */
    private boolean isLastResponseError = false;

    /**
     * Sets up one chatbot session: creates the {@link Ui}, points
     * {@link Storage} at {@code filePath}, and loads whatever tasks were
     * saved there last session. A file that can't be read at all isn't
     * fatal — the session starts from an empty list and warns about it — so
     * that a single unreadable file doesn't stop the user from using the
     * chatbot at all.
     *
     * @param filePath path to the data file this session loads from and
     *                 saves to.
     */
    public Myriad(String filePath) {
        this(filePath, true);
    }

    /**
     * Sets up one chatbot session, as {@link #Myriad(String)} does, but lets
     * the caller say whether messages are printed to the console. A GUI
     * session passes false: it shows the reply in a dialog box instead.
     *
     * @param filePath           path to the data file this session loads from
     *                           and saves to.
     * @param isEchoingToConsole whether messages are also printed to standard output.
     */
    public Myriad(String filePath, boolean isEchoingToConsole) {
        this.ui = new Ui(isEchoingToConsole);
        this.storage = new Storage(filePath);

        LoadedData loaded = loadSavedData(storage);
        this.tasks = loaded.tasks();
        this.skippedLines = loaded.skippedLines();
        this.loadErrorMessage = loaded.errorMessage();

        // Either the whole file was unreadable or some lines were skipped, never both.
        assert loadErrorMessage == null || skippedLines.isEmpty()
                : "a failed load should not also report skipped lines";
    }

    /**
     * What a session starts with after trying to load its data file. A
     * record lets {@link #loadSavedData} hand back all three values at once,
     * since a method cannot assign the constructor's final fields itself.
     *
     * @param tasks        the tasks to start the session with.
     * @param skippedLines descriptions of saved lines that could not be loaded.
     * @param errorMessage why the whole file could not be read, or null if it was read.
     */
    private record LoadedData(TaskList tasks, List<String> skippedLines, String errorMessage) {
    }

    /**
     * Loads the saved tasks from {@code storage}, falling back to an empty
     * list if the file cannot be read at all, so that a single unreadable
     * file does not stop the session from starting.
     *
     * @param storage the storage to load from.
     * @return the loaded tasks and any problems to report after the greeting.
     */
    private static LoadedData loadSavedData(Storage storage) {
        try {
            LoadResult result = storage.load();
            return new LoadedData(new TaskList(result.tasks()), result.skippedLines(), null);
        } catch (MyriadException e) {
            return new LoadedData(new TaskList(), List.of(), e.getMessage());
        }
    }

    /**
     * Runs one console chatbot session: greets the user, warns about
     * anything that couldn't be loaded, then takes lines from the {@link Ui}
     * until a command says the session is over or the input runs out, and
     * finally says goodbye. Each line is carried out by
     * {@link #executeLine}, the same method the GUI goes through, so whether
     * to stop is the command's answer rather than a keyword this loop checks
     * for.
     */
    public void run() {
        showStartupMessages();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            // Each line is a reply of its own, so the recorded text is cleared
            // rather than left to grow for the whole session.
            ui.startResponse();
            isExit = executeLine(ui.readCommand());
        }
        ui.showFarewell();
    }

    /**
     * Returns the opening message of a GUI session: the greeting, followed by
     * any warning about saved data that could not be read. This is what
     * {@link #run()} shows before its loop, packaged as text because a GUI
     * has no loop to hang it off.
     *
     * @return the greeting and any load warnings.
     */
    public String getGreeting() {
        ui.startResponse();
        showStartupMessages();
        return ui.getResponse();
    }

    /**
     * Shows the greeting, then any warning about saved data that could not
     * be loaded. Shared by {@link #run()} and {@link #getGreeting()} so the
     * console and the GUI always open a session with the same messages.
     */
    private void showStartupMessages() {
        ui.showGreeting();
        if (loadErrorMessage != null) {
            ui.showLoadingError(loadErrorMessage);
        }
        if (!skippedLines.isEmpty()) {
            ui.showLoadWarning(skippedLines);
        }
    }

    /**
     * Runs one line of user input and returns what the chatbot says back.
     * Goes through {@link #executeLine}, as each iteration of {@link #run()}
     * does, so that the GUI and the console answer any given line the same
     * way.
     *
     * @param input the raw line the user typed, whitespace included.
     * @return the chatbot's reply.
     */
    public String getResponse(String input) {
        assert input != null : "the GUI always passes the text field's contents";
        ui.startResponse();
        isLastResponseError = false;
        // Stripped here because readCommand() does it for the console, and
        // Parser expects a tidy line from either front end.
        boolean isExit = executeLine(input.strip());
        if (isExit) {
            // run() says goodbye after its loop, since ExitCommand.execute
            // is empty; with no loop, the farewell belongs in the reply.
            ui.showFarewell();
            isExitRequested = true;
        }
        return ui.getResponse();
    }

    /**
     * Parses and carries out one stripped line of input, showing any error
     * through the {@link Ui}, and returns whether the command asked to end
     * the session.
     *
     * Both parsing and executing throw {@link MyriadException} instead of
     * showing an error themselves, so this is the single place that catches
     * it and shows it; {@link Ui#showError} adds the error exclamation rather
     * than every message repeating it. Save failures arrive as
     * {@code MyriadException} too, so they are reported like any other
     * command error instead of crashing the program.
     *
     * @param strippedLine one line of input, already whitespace-stripped.
     * @return true if the command was an exit command; false otherwise,
     *         including when the line was rejected with an error.
     */
    private boolean executeLine(String strippedLine) {
        try {
            Command command = Parser.parse(strippedLine);
            command.execute(tasks, ui, storage);
            return command.isExit();
        } catch (MyriadException e) {
            ui.showError(e.getMessage());
            isLastResponseError = true;
            return false;
        }
    }

    /**
     * Returns whether a command has ended the session, so that a GUI knows to
     * close its window.
     *
     * @return true once an exit command has been run.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns whether the reply from the latest {@link #getResponse} call was
     * an error message, so that a GUI can show it differently from an
     * ordinary answer.
     *
     * @return true if the latest line was rejected with an error; false if it
     *         succeeded or no line has been run yet.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns whether anything went wrong loading the saved data, either the
     * whole file or some of its lines. A GUI uses this to show the greeting,
     * which carries the warning, in a style that draws the user's eye.
     *
     * @return true if the greeting includes a load warning.
     */
    public boolean hasLoadProblem() {
        return loadErrorMessage != null || !skippedLines.isEmpty();
    }

    /**
     * Starts one console chatbot session on {@link #DEFAULT_DATA_FILE}.
     *
     * @param args ignored; the data file location is fixed in code rather
     *             than taken from the command line.
     */
    public static void main(String[] args) {
        new Myriad(DEFAULT_DATA_FILE).run();
    }
}
