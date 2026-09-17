package harvey;

import harvey.command.Command;
import harvey.parser.Parser;
import harvey.storage.Storage;
import harvey.task.TaskList;
import harvey.ui.Ui;
/**
 * The Harvey chatbot.
 * <p>
 * Harvey stores three kinds of task ({@link Todo}, {@link Deadline} and {@link Event}),
 * lists them back on request, remembers which have been marked as done, and saves the
 * list to disk after every change. Input it cannot carry out is reported through
 * {@link HarveyException} instead of crashing.
 * <p>
 * This class is only the conductor. Reading and printing belong to {@link Ui}, working out
 * what the user meant belongs to {@link Parser}, holding the tasks belongs to
 * {@link TaskList}, and the file belongs to {@link Storage}. What is left here is the
 * order those four are used in.
 * <p>
 * There are two ways in. {@link #run()} drives the text interface, reading and printing
 * until the user stops. {@link #getResponse(String)} answers a single line and hands the
 * reply back, which is what the graphical interface needs: the window, not Harvey, decides
 * when the next line arrives.
 */
public class Harvey {
    /** Folder holding the save file, relative to the folder the program is started from. */
    private static final String DATA_FOLDER = "data";

    /** Name of the save file inside {@link #DATA_FOLDER}. */
    private static final String DATA_FILE = "harvey.txt";

    /**
     * Stands in for a command class name when the input was rejected. No command was
     * built in that case, but the graphical interface still needs something to style by.
     */
    private static final String ERROR_COMMAND_TYPE = "ErrorCommand";

    /** Owns the wording of every reply, and reads input for the text interface. */
    private final Ui ui;

    /** Loads the tasks at startup and writes them back after every change. */
    private final Storage storage;

    /** The tasks Harvey is keeping. */
    private final TaskList tasks;

    /**
     * Anything that went wrong while loading the save file, or null if it loaded cleanly.
     * Held rather than printed so that each front end can show it just after its greeting,
     * instead of above it.
     */
    private final String startupWarning;

    /** Whether the last command answered by {@link #getResponse(String)} was a goodbye. */
    private boolean isExit;

    /**
     * Name of the command class that answered the last line, or {@link #ERROR_COMMAND_TYPE}
     * if that line was rejected. Null until the first line is answered.
     */
    private String commandType;

    /** Creates a chatbot saving to the usual {@code data/harvey.txt}. */
    public Harvey() {
        this(DATA_FOLDER, DATA_FILE);
    }

    /**
     * Creates a chatbot that saves to the given file, loading whatever it already holds.
     *
     * @param folderName the folder holding the save file.
     * @param fileName   the name of the save file.
     */
    public Harvey(String folderName, String fileName) {
        this.ui = new Ui();
        this.storage = new Storage(folderName, fileName);

        // Loading cannot report itself here, because the greeting has not been shown yet.
        // Both the tasks and any complaint about the file are therefore kept as fields.
        String warning = null;
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());

            // Damaged lines are skipped rather than reported one by one, but the user is
            // told that some tasks are missing so the silence is not mistaken for the
            // file having been empty.
            if (storage.getSkippedLines() > 0) {
                warning = ui.formatError("I could not understand " + storage.getSkippedLines()
                        + " line(s) in your saved file, so those tasks were left out. "
                        + "Everything else was loaded. " + describeBackup()).trim();
            }
        } catch (HarveyException e) {
            // Being unable to read the saved file is not a reason to refuse to start,
            // so Harvey says what went wrong and carries on with nothing loaded.
            warning = ui.formatError(e.getMessage() + " Starting with an empty list. "
                    + describeBackup()).trim();
            loaded = new TaskList();
        }
        this.tasks = loaded;
        this.startupWarning = warning;
    }

    /**
     * Returns the sentence telling the user what became of a save file that could not be
     * fully loaded: where its backup is, or that saving is off because there is none.
     *
     * @return the sentence to add to a loading warning, or an empty string if no data was at risk.
     */
    private String describeBackup() {
        if (storage.getBackupPath() != null) {
            return "The original file is backed up at " + storage.getBackupPath() + ".";
        }
        if (!storage.isOverwriteSafe()) {
            return "I could not back it up either, so I won't save over it until you move it "
                    + "somewhere safe and restart me.";
        }
        return "";
    }

    /**
     * Returns the welcome message, shown before the user has typed anything.
     *
     * @return the greeting.
     */
    public String getGreeting() {
        return ui.formatGreeting();
    }

    /**
     * Returns what went wrong while loading the save file, if anything.
     *
     * @return the warning to show just after the greeting, or null if the file loaded cleanly.
     */
    public String getStartupWarning() {
        return startupWarning;
    }

    /**
     * Answers one line of input.
     * <p>
     * This is the whole body of the old loop, minus the reading and printing: the line is
     * parsed, the resulting command is carried out, and what it has to say is handed back
     * rather than printed. Nothing is thrown, because a caller drawing a window has no
     * better way to report a mistake than showing it, which is what happens here.
     *
     * @param input the line the user typed.
     * @return Harvey's reply, which may be a report of what went wrong.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            String response = command.execute(tasks, ui, storage);
            isExit = command.isExit();
            commandType = command.getClass().getSimpleName();
            return response;
        } catch (HarveyException e) {
            // getMessage() returns the explanation the thrower wrote for the user.
            isExit = false;
            commandType = ERROR_COMMAND_TYPE;
            return ui.formatError(e.getMessage());
        }
    }

    /**
     * Returns whether the last line answered by {@link #getResponse(String)} was a goodbye.
     * A graphical caller uses this to decide when to close its window.
     *
     * @return true if Harvey has been told to stop.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the name of the command class that answered the last line, or null if none
     * has been answered yet. The graphical interface uses it to color each reply by the
     * kind of command it came from.
     *
     * @return the simple class name of the last command, e.g. {@code "AddCommand"}.
     */
    public String getCommandType() {
        return commandType;
    }

    /** Greets the user, then answers commands until they say goodbye or the input ends. */
    public void run() {
        ui.printGreeting();
        if (startupWarning != null) {
            ui.printReply(startupWarning);
        }

        while (ui.hasNextCommand()) {
            ui.printReply(getResponse(ui.readCommand()));
            if (isExit) {
                return;
            }
        }

        // Reached only when the input ran out without a goodbye, e.g. the user pressed
        // Ctrl-D. Saying the same parting words there keeps the two endings alike.
        ui.printReply(ui.formatFarewell());
    }

    /**
     * Starts the chatbot's text interface.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        new Harvey().run();
    }
}
