package niulai;

import java.util.Objects;

import niulai.command.Command;
import niulai.command.Command.UndoAction;
import niulai.model.TaskList;
import niulai.service.Parser;
import niulai.service.Storage;
import niulai.service.StorageException;
import niulai.service.Ui;

/**
 * Runs the NiuLai command-line chatbot.
 */
public class NiuLai {
    /** The default location of the task data file. */
    public static final String DEFAULT_FILE_PATH = "data/niulai.txt";

    /** The component that persists tasks between chatbot sessions. */
    private final Storage storage;

    /** The tasks currently managed by the chatbot. */
    private TaskList tasks;

    /** The component that handles console interaction. */
    private final Ui ui;

    /** The component that interprets user commands. */
    private final Parser parser;

    /** The inverse action for the most recent successful state-changing command. */
    private UndoAction lastUndoAction;

    /**
     * Creates a chatbot backed by the specified task data file.
     *
     * @param filePath the path of the task data file
     */
    public NiuLai(String filePath) {
        this(filePath, new Ui());
    }

    /** Creates a chatbot backed by the default task data file. */
    public NiuLai() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a chatbot with a caller-provided user-interface component.
     *
     * <p>This constructor lets the command-processing logic be reused by interfaces other than
     * the command line, such as the JavaFX interface.</p>
     *
     * @param filePath the path of the task data file
     * @param ui the component used to present chatbot responses
     */
    public NiuLai(String filePath, Ui ui) {
        this.ui = Objects.requireNonNull(ui, "ui");
        parser = new Parser();
        storage = new Storage(filePath);
        tasks = new TaskList();
    }

    /** Runs the chatbot until the user enters {@code bye} or input ends. */
    public void run() {
        startSession();

        while (ui.hasNextLine()) {
            String command = ui.readCommand();

            if (processCommand(command)) {
                break;
            }
        }
    }

    /** Starts a chatbot session by showing the welcome message and loading saved tasks. */
    public void startSession() {
        ui.showWelcome();
        tasks = loadTasks();
        lastUndoAction = null;
    }

    /**
     * Processes one command using the current task list.
     *
     * @param input the command entered by the user
     * @return whether the command ends the chatbot session
     */
    public boolean processCommand(String input) {
        String command = Objects.requireNonNull(input, "input").strip();
        ui.showSeparator();

        try {
            Command parsedCommand = parser.parseCommand(command, tasks.size());
            if (parsedCommand.isUndo()) {
                undoLastCommand();
                return false;
            }

            UndoAction undoAction = parsedCommand.execute(tasks, ui, storage);
            if (undoAction != null) {
                lastUndoAction = undoAction;
            }
            return parsedCommand.isExit();
        } catch (NiuLaiException e) {
            ui.showError(e.getMessage());
            return false;
        }
    }

    /** Undoes the most recent successful state-changing command, if one exists. */
    private void undoLastCommand() throws NiuLaiException {
        if (lastUndoAction == null) {
            ui.showNoUndo();
            return;
        }

        lastUndoAction.undo(tasks, storage);
        lastUndoAction = null;
        ui.showUndo();
    }

    /**
     * Loads the saved task list, starting with an empty list if loading fails.
     *
     * @return the saved task list or an empty list when no usable data is available
     */
    private TaskList loadTasks() {
        try {
            Storage.LoadResult result = storage.load();
            if (!result.issues().isEmpty()) {
                ui.showRecoveryWarning(result.issues().stream()
                        .map(Storage.LoadIssue::lineNumber)
                        .toList());
            }
            return result.tasks();
        } catch (StorageException e) {
            ui.showLoadingError(e.getUserMessage());
            return new TaskList();
        }
    }

    /** Starts the chatbot with its default data file. */
    public static void main(String[] args) {
        new NiuLai().run();
    }
}
