package duncan;

import java.util.List;

import duncan.command.Command;
import duncan.task.TaskList;

/**
 * A simple task manager. Carries out the commands the user gives it and
 * saves the task list to disk after every change, so it survives between
 * runs.
 *
 * <p>The same instance backs both front ends: {@link #run()} drives the
 * console app, while a GUI feeds it one line at a time through
 * {@link #getResponse(String)}.
 */
public class Duncan {
    /** Where the task list is kept between runs, relative to the project root. */
    private static final String DATA_FILE_PATH = "data/duncan.txt";

    private static final String MESSAGE_UNEXPECTED_ERROR = "HEY! something went wrong: ";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /** Whether the last command handled was the one that ends the session. */
    private boolean isExit = false;

    /**
     * Sets up a Duncan wired to the given save file: creates its {@link Ui},
     * a {@link Storage} pointing at {@code filePath}, and loads the task
     * list saved there (or an empty list, if there is none yet). Any
     * warnings from loading are queued in the {@link Ui}, so they are shown
     * with the welcome message.
     *
     * @param filePath path to the save file, relative to the directory the
     *                 program is run from
     */
    public Duncan(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(storage.load());
        storage.getLoadWarnings().forEach(ui::showWarning);
    }

    /** Sets up a Duncan wired to the default save file. */
    public Duncan() {
        this(DATA_FILE_PATH);
    }

    /**
     * Returns the greeting to show at the start of a session, without the
     * console app's banner and dividers.
     */
    public String getWelcome() {
        ui.showGreeting();
        return ui.flushOutput();
    }

    /**
     * Returns the same greeting as {@link #getWelcome()}, line by line with
     * each line's severity, so a GUI can style any load warning that comes
     * before it.
     */
    public List<Ui.MessageLine> getWelcomeLines() {
        ui.showGreeting();
        return ui.flushLines();
    }

    /** Returns the ASCII-art banner the console app opens with, for a GUI to show as a heading. */
    public String getBanner() {
        return ui.getBanner();
    }

    /**
     * Carries out one line of user input and returns everything Duncan has
     * to say in reply, including the message of any error the command ran
     * into. This is the whole of Duncan's behaviour as far as a GUI is
     * concerned.
     *
     * @param input one line as typed by the user, e.g. {@code "todo read book"}
     * @return Duncan's reply, as one or more lines of text.
     */
    public String getResponse(String input) {
        carryOut(input);
        return ui.flushOutput();
    }

    /**
     * Carries out one line of user input like {@link #getResponse(String)},
     * but returns the reply line by line with each line's severity, so a GUI
     * can tell errors and warnings apart from ordinary output.
     *
     * @param input one line as typed by the user, e.g. {@code "todo read book"}
     * @return Duncan's reply, one entry per line.
     */
    public List<Ui.MessageLine> getResponseLines(String input) {
        carryOut(input);
        return ui.flushLines();
    }

    /** Runs one line of input, leaving whatever Duncan has to say waiting in the {@link Ui}. */
    private void carryOut(String input) {
        try {
            Command c = Parser.parse(input);
            c.execute(tasks, ui, storage);
            isExit = c.isExit();
        } catch (DuncanException e) {
            ui.showError(e.getMessage());
        } catch (RuntimeException e) {
            // A safety net for bugs: no input should crash the program or leave the user without a reply.
            ui.showError(MESSAGE_UNEXPECTED_ERROR + e.getMessage());
        }
    }

    /** Returns whether the last command handled asked to end the session. */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Greets the user, then reads and carries out commands until told to
     * exit, or until the console input ends.
     */
    public void run() {
        ui.showWelcome();
        flushToConsole();
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            ui.showLine();
            flushToConsole();

            System.out.print(getResponse(fullCommand));

            ui.showLine();
            ui.showBlankLine();
            flushToConsole();
        }
    }

    /** Prints whatever the {@link Ui} has collected so far to the console. */
    private void flushToConsole() {
        System.out.print(ui.flushOutput());
    }

    /** Program entry point for the console app. */
    public static void main(String[] args) {
        new Duncan().run();
    }
}
