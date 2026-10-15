package samantha;

import java.util.ArrayList;
import java.util.List;

import samantha.command.Command;
import samantha.command.CommandContext;
import samantha.exception.CorruptedNoteFileException;
import samantha.exception.CorruptedTaskFileException;
import samantha.exception.NoteFileReadException;
import samantha.exception.SamanthaException;
import samantha.exception.TaskFileReadException;
import samantha.exception.UnknownCommandException;
import samantha.model.NoteList;
import samantha.model.TaskList;
import samantha.parser.Parser;
import samantha.storage.NoteStorage;
import samantha.storage.Storage;
import samantha.ui.Ui;

/**
 * Coordinates Samantha's command loop and its task, storage, and UI components.
 */
public class Samantha {
    private static final String GREETING = "Hello. I'm here. What would you like to do today?";
    private static final String CORRUPTED_FILE_WARNING =
            "Warning: The saved task file is corrupted. Starting with an empty task list.";
    private static final String FILE_READ_WARNING =
            "Warning: I couldn't read the saved tasks. Starting with an empty task list.";
    private static final String CORRUPTED_NOTE_FILE_WARNING =
            "Warning: The saved note file is corrupted. Starting with an empty note list.";
    private static final String NOTE_FILE_READ_WARNING =
            "Warning: I couldn't read the saved notes. Starting with an empty note list.";
    private static final String GOODBYE = "Bye. Let's talk next time!";
    private static final int UNKNOWN_COMMANDS_BEFORE_HELP = 3;
    private static final String CONSOLE_BANNER = " ____    _    __  __    _    _   _ _____ _   _    _    \n"
            + "/ ___|  / \\  |  \\/  |  / \\  | \\ | |_   _| | | |  / \\   \n"
            + "\\___ \\ / _ \\ | |\\/| | / _ \\ |  \\| | | | |_| | / _ \\  \n"
            + " ___) / ___ \\| |  | |/ ___ \\| |\\  | | | |  _  |/ ___ \\ \n"
            + "|____/_/   \\_\\_|  |_/_/   \\_\\_| \\_| |_| |_| |_/_/   \\_\\\n";

    private final TaskList tasks = new TaskList();
    private final NoteList notes = new NoteList();
    private final Storage storage;
    private final NoteStorage noteStorage;
    private boolean isInitialized;
    private boolean isExitRequested;
    private boolean isLastResponseError;
    private boolean isLastResponseUnknownCommand;
    private boolean shouldOpenHelpGuide;
    private int unknownCommandCount;
    private String startupWarning = "";
    private Command lastUndoableCommand;

    /**
     * Creates an empty Samantha instance using the default storage location.
     */
    public Samantha() {
        this(new Storage(), new NoteStorage());
    }

    /**
     * Creates an application instance with the supplied collaborators.
     *
     * @param storage task persistence handler
     */
    Samantha(Storage storage) {
        this(storage, new NoteStorage());
    }

    /**
     * Creates an application instance with isolated task and note storage.
     *
     * @param storage task persistence handler
     * @param noteStorage note persistence handler
     */
    Samantha(Storage storage, NoteStorage noteStorage) {
        assert storage != null : "Samantha must have a storage handler";
        assert noteStorage != null : "Samantha must have a note storage handler";
        this.storage = storage;
        this.noteStorage = noteStorage;
    }

    /**
     * Loads the persisted tasks into this application's task list.
     *
     * @throws TaskFileReadException if the task file cannot be read
     * @throws CorruptedTaskFileException if a persisted task record is malformed
     */
    private void loadTasks() throws TaskFileReadException, CorruptedTaskFileException {
        tasks.addAll(storage.load());
    }

    /**
     * Loads the persisted notes into this application's note list.
     *
     * @throws NoteFileReadException if the note file cannot be read
     * @throws CorruptedNoteFileException if a persisted note is malformed
     */
    private void loadNotes() throws NoteFileReadException, CorruptedNoteFileException {
        notes.addAll(noteStorage.load());
    }

    /**
     * Returns the assistant messages that should open a new conversation.
     *
     * @return greeting and any storage warning
     */
    public List<String> getInitialResponses() {
        initialize();
        List<String> messages = new ArrayList<>();
        messages.add(GREETING);
        if (!startupWarning.isEmpty()) {
            messages.add(startupWarning);
        }
        return List.copyOf(messages);
    }

    /**
     * Processes one user command and returns Samantha's response.
     *
     * @param input command entered by the user
     * @return command response, error message, or farewell
     */
    public String getResponse(String input) {
        initialize();
        assert isInitialized : "Samantha must be initialized before handling commands";
        isLastResponseError = false;
        isLastResponseUnknownCommand = false;
        shouldOpenHelpGuide = false;
        try {
            Command command = Parser.parse(input);
            assert command != null : "Parsing a valid command must produce a command";
            String response = command.isUndo()
                    ? undoLastCommand()
                    : executeAndRecord(command);
            isExitRequested = command.isExit();
            unknownCommandCount = 0;
            return isExitRequested ? GOODBYE : response;
        } catch (UnknownCommandException e) {
            isLastResponseError = true;
            isLastResponseUnknownCommand = true;
            unknownCommandCount++;
            shouldOpenHelpGuide = unknownCommandCount >= UNKNOWN_COMMANDS_BEFORE_HELP;
            if (shouldOpenHelpGuide) {
                unknownCommandCount = 0;
            }
            return e.getMessage();
        } catch (SamanthaException e) {
            isLastResponseError = true;
            unknownCommandCount = 0;
            return e.getMessage();
        }
    }

    /**
     * Executes a regular command and records it when it changes application state.
     *
     * @param command command to execute
     * @return command response
     * @throws SamanthaException if command execution fails
     */
    private String executeAndRecord(Command command) throws SamanthaException {
        CommandContext context = new CommandContext(tasks, notes, storage, noteStorage);
        String response = command.execute(context);
        if (command.isUndoable()) {
            lastUndoableCommand = command;
        }
        return response;
    }

    /**
     * Undoes the latest successful undoable command, if one exists.
     *
     * @return undo confirmation or a message explaining that there is no history
     * @throws SamanthaException if undoing the command fails
     */
    private String undoLastCommand() throws SamanthaException {
        if (lastUndoableCommand == null) {
            return "There is nothing to undo.";
        }
        CommandContext context = new CommandContext(tasks, notes, storage, noteStorage);
        String response = lastUndoableCommand.undo(context);
        lastUndoableCommand = null;
        return response;
    }

    /**
     * Returns whether the most recently handled command requested application exit.
     *
     * @return {@code true} after a {@code bye} command
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns whether the most recently handled command produced an error reply.
     *
     * @return {@code true} when {@link #getResponse(String)} returned an exception message
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns whether the most recently handled command was an unrecognized command word.
     *
     * @return {@code true} after an unknown command
     */
    public boolean isLastResponseUnknownCommand() {
        return isLastResponseUnknownCommand;
    }

    /**
     * Returns whether the GUI should open the command guide after repeated unknown commands.
     *
     * @return {@code true} after three consecutive unrecognized commands
     */
    public boolean shouldOpenHelpGuide() {
        return shouldOpenHelpGuide;
    }

    /**
     * Clears the consecutive unknown-command count and any pending help-window request.
     */
    public void resetUnknownCommandStreak() {
        unknownCommandCount = 0;
        shouldOpenHelpGuide = false;
    }

    /**
     * Loads saved tasks once and records any user-facing storage warning.
     */
    private void initialize() {
        if (isInitialized) {
            return;
        }

        try {
            loadTasks();
        } catch (CorruptedTaskFileException e) {
            addStartupWarning(CORRUPTED_FILE_WARNING);
        } catch (TaskFileReadException e) {
            addStartupWarning(FILE_READ_WARNING);
        }

        try {
            loadNotes();
        } catch (CorruptedNoteFileException e) {
            addStartupWarning(CORRUPTED_NOTE_FILE_WARNING);
        } catch (NoteFileReadException e) {
            addStartupWarning(NOTE_FILE_READ_WARNING);
        }
        isInitialized = true;
    }

    /**
     * Adds a user-facing warning recorded during startup.
     *
     * @param warning warning to display
     */
    private void addStartupWarning(String warning) {
        if (startupWarning.isEmpty()) {
            startupWarning = warning;
        } else {
            startupWarning += System.lineSeparator() + warning;
        }
    }

    /**
     * Runs the application until the user enters the exit command.
     */
    public void run() {
        Ui ui = new Ui();
        initialize();
        showStartupWarning(ui);
        ui.showWelcome(CONSOLE_BANNER, GREETING);
        runCommandLoop(ui);
        ui.showGoodbye();
    }

    /**
     * Displays the warning recorded while loading persisted tasks, if any.
     *
     * @param ui console interaction handler
     */
    private void showStartupWarning(Ui ui) {
        if (!startupWarning.isEmpty()) {
            ui.showResponse(startupWarning);
        }
    }

    /**
     * Processes commands until the user requests application exit.
     *
     * @param ui console interaction handler
     */
    private void runCommandLoop(Ui ui) {
        while (!isExitRequested) {
            String response = getResponse(ui.readCommand());
            if (!isExitRequested) {
                ui.showResponse(response);
            }
        }
    }

    /**
     * Starts Samantha using the default storage location.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Samantha().run();
    }
}
