package goat;

import goat.command.Command;
import goat.parser.Parser;
import goat.storage.Storage;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * A simple command-line chatbot.
 * Currently it greets the user, stores todos, deadlines and events,
 * lists the stored tasks on request, lets a task be marked as done
 * or deleted, and exits when the user types "bye".
 *
 * Every job the program does now belongs to some other class: {@link Ui} does
 * the talking, {@link TaskList} holds the tasks, {@link Storage} reads and
 * writes the save file, {@link Parser} makes sense of what the user typed, and
 * a {@link Command} carries one instruction out.
 *
 * What is left here is the only thing none of them could own: the shape of a
 * session. Set the four of them up, greet, then read a line and run it, over
 * and over, until something says to stop or the input runs out.
 *
 * Notice that this class no longer mentions a single command by name. Adding
 * one means writing a Command subclass and naming it in Parser; nothing here
 * changes.
 */
public class Goat {

    /**
     * Where the tasks are kept, relative to the folder the app is started from.
     *
     * Named here, at the outermost edge of the program, so that both entry
     * points -- the terminal main() below and the window's Main -- open the
     * same file without either one deciding where it lives.
     */
    public static final String DEFAULT_SAVE_PATH = "data/goat.txt";

    /** Does all the talking to and reading from the user. */
    private final Ui ui;

    /** The tasks being tracked, loaded at start-up and saved after each change. */
    private final TaskList tasks;

    /** The save file the tasks are read from and written back to. */
    private final Storage storage;

    /** Set once a command has asked Goat to stop; only the GUI reads it. */
    private boolean isExitRequested = false;

    /**
     * Whether the last reply was a complaint rather than a result.
     *
     * The terminal marks an error by prefixing it with "OOPS!!! ", which works
     * because everything arrives as a stream of text. A window can do better
     * than a prefix, but only if it is told; this is how it is told.
     */
    private boolean isLastResponseAnError = false;

    /**
     * Sets up a chatbot with a task list read from the given file.
     *
     * The constructor is the right place for the loading because a Goat that
     * has not read its tasks is not ready to be used, and a constructor is the
     * one method that is guaranteed to run before anything else can.
     *
     * @param filePath where the tasks are kept, e.g. "data/goat.txt"
     */
    public Goat(String filePath) {
        this(filePath, new Ui());
    }

    private Goat(String filePath, Ui ui) {
        this.ui = ui;
        this.storage = new Storage(filePath);
        // Greet before loading, not after: loading can fail, and a complaint
        // about the save file should not be the first thing the user sees.
        ui.showWelcome();
        // Assigned to a local first because a final field cannot be set twice,
        // and the catch below needs a second go at deciding what it holds.
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (GoatException e) {
            // A save file that cannot be read should not stop the program, but
            // the user is warned, because the next change will overwrite it.
            ui.showLoadingError(e.getMessage());
            isLastResponseAnError = true;
            loaded = new TaskList();
        }
        this.tasks = loaded;
        // Whichever way the load went -- file read, file missing, file damaged
        // -- the chatbot has to come out of the constructor ready to use.
        assert this.ui != null : "Goat built without a Ui";
        assert this.storage != null : "Goat built without a Storage";
        assert this.tasks != null : "Goat built without a TaskList";
    }

    /**
     * Creates a chatbot for a graphical front end.
     *
     * The difference is only in the Ui: this one buffers its replies instead of
     * printing them, so the caller can put them on screen itself. Everything
     * below -- the parser, the task list, the save file -- is the same.
     *
     * @param filePath where the tasks are kept, e.g. "data/goat.txt"
     * @return a chatbot whose replies are read with {@link #getResponse(String)}
     */
    public static Goat forGui(String filePath) {
        return new Goat(filePath, Ui.forGui());
    }

    /**
     * Reads and carries out commands until one of them says to stop, or until
     * the input runs out. The greeting has already been shown by the constructor.
     */
    public void run() {
        boolean isExit = false;
        // The second test matters when commands are piped in from a file and
        // the file ends without a "bye".
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            // One catch for the whole loop: parsing the line and running it
            // both just throw when something is wrong, and this decides how the
            // problem is shown. Catching inside the loop means a bad command
            // never ends the program.
            try {
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (GoatException e) {
                ui.showError(e.getMessage());
            }
        }

        // Outside the loop, so the farewell is shown whether the user typed
        // "bye" or the input simply ran out.
        ui.showGoodbye();
        ui.close();
    }

    /**
     * Carries out one command and returns what the user should be shown.
     *
     * This is the graphical front end's equivalent of one turn of
     * {@link #run()}'s loop. The loop itself stays in run(), because a window
     * has its own loop already and does not want a second one.
     *
     * @param fullCommand one whole line as the user typed it
     * @return the reply, as plain text, possibly spanning several lines
     */
    public String getResponse(String fullCommand) {
        isLastResponseAnError = false;
        try {
            Command command = Parser.parse(fullCommand);
            command.execute(tasks, ui, storage);
            if (command.isExit()) {
                // run() shows the farewell after its loop ends; there is no
                // loop here, so the exiting command has to bring it with it.
                isExitRequested = true;
                ui.showGoodbye();
            }
        } catch (GoatException e) {
            isLastResponseAnError = true;
            ui.showError(e.getMessage());
        }
        return ui.takeResponse();
    }

    /**
     * Reports whether the last command asked Goat to stop.
     *
     * @return true once a command has asked to exit
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Reports whether the last reply was a complaint rather than a result.
     *
     * @return true if the last command could not be carried out
     */
    public boolean isLastResponseAnError() {
        return isLastResponseAnError;
    }

    /**
     * Returns the greeting, for a front end that has to display it itself.
     *
     * @return the welcome message, plus any complaint about the save file
     */
    public String getWelcomeMessage() {
        return ui.takeResponse();
    }

    /**
     * Starts Goat.
     *
     * @param args ignored; Goat takes no command line arguments, since
     *             everything it needs is typed at the prompt once it is running
     */
    public static void main(String[] args) {
        new Goat(DEFAULT_SAVE_PATH).run();
    }
}
