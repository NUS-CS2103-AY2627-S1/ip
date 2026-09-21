package ted;

import ted.command.Command;
import ted.task.TaskList;

/**
 * The core of the Ted chatbot.
 * Ted stores todos, deadlines and events, and lets the user list, find, mark,
 * tag and delete them, until the user enters {@code bye}.
 * <p>
 * The same conversation is offered two ways: {@link #run()} drives it from the
 * terminal, while {@link #getResponse(String)} answers one message at a time so
 * that the GUI can drive it instead. Both share the work below.
 * <p>
 * This class only wires the parts together and runs the conversation loop: the
 * talking belongs to {@link Ui}, the tasks to {@link TaskList}, the save file
 * to {@link Storage}, reading the user's words to {@link Parser}, and the work
 * itself to the {@link Command} classes.
 */
public class Ted {
    /** Where the tasks are kept between runs, relative to where Ted is run from. */
    private static final String DATA_FILE_PATH = "data/ted.txt";

    /** Handles what Ted says and what the user types. */
    private final Ui ui;

    /** Loads the tasks at startup and saves them after every change. */
    private final Storage storage;

    /** The tasks entered so far. */
    private TaskList tasks;

    /**
     * Why the save file could not be read, or {@code null} if it loaded.
     * Kept until the greeting so that the warning follows the welcome message
     * instead of appearing above it.
     */
    private String loadErrorMessage;

    /** Whether the last handled command asked Ted to stop. */
    private boolean isExit = false;

    /**
     * Whether Ted's last message explained a problem rather than confirming
     * what was done, so that the GUI can make it stand out.
     */
    private boolean isError = false;

    /**
     * Creates a Ted that keeps its tasks in the usual place.
     * JavaFX builds the application through a no-argument constructor, so this
     * one exists for the GUI to use.
     */
    public Ted() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a Ted whose tasks are kept in the given file, restoring whatever
     * was saved there previously.
     *
     * @param filePath where to keep the tasks, e.g. {@code data/ted.txt}.
     */
    public Ted(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            // The list must be complete before the user is greeted, so that every
            // command that follows can rely on it.
            tasks = new TaskList(storage.load());
        } catch (TedException e) {
            // An unreadable save file is not worth refusing to start over.
            loadErrorMessage = e.getMessage();
            tasks = new TaskList();
        }
    }

    /** Greets the user, then handles commands until the conversation ends. */
    public void run() {
        ui.printReply(getGreeting());

        // hasNextCommand() also stops the loop when the input stream ends,
        // e.g. on Ctrl-D or at the end of a piped file.
        while (!isExit && ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.isBlank()) {
                // A stray blank line is not worth a reply.
                continue;
            }
            ui.printReply(getResponse(input));
        }

        if (!isExit) {
            // The input ran out before the user said bye, but they still get one.
            ui.showGoodbye();
            ui.printReply(ui.flush());
        }
    }

    /**
     * Handles one message from the user and returns what Ted says back.
     *
     * @param input one line of user input, as typed.
     * @return Ted's reply, or an empty string if there was nothing to reply to.
     */
    public String getResponse(String input) {
        // The GUI passes its text field on as typed, so stray spaces are removed
        // here, where both front ends meet, rather than in each of them.
        String trimmedInput = input.strip();
        isError = false;
        if (trimmedInput.isEmpty()) {
            // A stray blank line is not worth a reply.
            return "";
        }

        Command command;
        try {
            command = Parser.parse(trimmedInput);
        } catch (TedException e) {
            // Every problem Ted can recognize is recoverable, so the message is
            // shown and the conversation continues with the next command.
            isError = true;
            ui.showError(e.getMessage());
            return ui.flush();
        }

        isExit = command.isExit();
        if (isExit) {
            // Quitting gets the goodbye rather than an ordinary reply.
            ui.showGoodbye();
            return ui.flush();
        }

        assert tasks != null : "the constructor sets up a task list even when loading fails";
        try {
            command.execute(tasks, ui, storage);
        } catch (TedException e) {
            isError = true;
            ui.showError(e.getMessage());
        }
        return ui.flush();
    }

    /**
     * Returns Ted's opening message, including any complaint about the save
     * file and where a copy of it was kept.
     *
     * @return the greeting to show before the user has typed anything.
     */
    public String getGreeting() {
        // A greeting that warns about the save file needs the user's attention as much as any error.
        isError = loadErrorMessage != null || storage.getSkippedLineCount() > 0;
        ui.showWelcome();
        if (loadErrorMessage != null) {
            ui.showLoadingError(loadErrorMessage);
        }
        if (storage.getSkippedLineCount() > 0) {
            ui.showSkippedLines(storage.getSkippedLineCount());
        }
        storage.getBackupFile().ifPresent(ui::showBackup);
        return ui.flush();
    }

    /**
     * Returns whether the last command handled asked Ted to stop.
     * The GUI uses this to close its window once the goodbye has been shown.
     *
     * @return {@code true} if the user has said bye.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns whether Ted's last message, the greeting or a reply, explained
     * a problem. The GUI uses this to show such messages differently.
     *
     * @return {@code true} if the last message reported an error or a warning.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Starts Ted in the terminal. The window is started by {@link Launcher} instead.
     *
     * @param args command-line arguments, which Ted does not use.
     */
    public static void main(String[] args) {
        new Ted().run();
    }
}
