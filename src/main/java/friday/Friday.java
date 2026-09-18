package friday;

import java.io.IOException;

import friday.command.Command;
import friday.exception.FridayException;
import friday.model.TaskList;
import friday.parser.Parser;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    /**
     * Creates the application coordinator.
     */
    public Friday() {
    }

    /**
     * Starts the chatbot and processes user commands until the user exits.
     *
     * @param args Command-line arguments, which are not used by this program.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showGreeting();

        Storage storage = new Storage("data/friday.txt");
        Storage archiveStorage = new Storage("data/archive.txt");
        Parser parser = new Parser();
        SessionState session = loadSession(ui, storage, "saved tasks", "Saving is disabled for this session.");
        SessionState archiveSession = loadSession(ui, archiveStorage, "archive",
                "Archive commands are disabled for this session.");
        processCommands(ui, parser, storage, archiveStorage, session, archiveSession);
        ui.close();
    }

    /**
     * Loads the task list and determines whether changes can be persisted.
     *
     * @param ui UI used to report malformed or unreadable saved data.
     * @param storage Storage used to load tasks.
     * @param dataDescription Description of the data being loaded.
     * @param disabledMessage Message displayed when persistence is disabled.
     * @return The loaded task list and its persistence state.
     */
    private static SessionState loadSession(Ui ui, Storage storage, String dataDescription, String disabledMessage) {
        try {
            Storage.LoadResult loadResult = storage.load();
            // Storage.load() must always return a result when it completes normally.
            assert loadResult != null : "A successful load must provide a result.";
            TaskList tasks = new TaskList(loadResult.getTasks());
            for (String warning : loadResult.getWarnings()) {
                ui.showError(warning);
            }
            return new SessionState(tasks, true);
        } catch (IOException e) {
            ui.showError("I couldn't read the " + dataDescription + ", so I've started with an empty list. "
                    + disabledMessage);
            return new SessionState(new TaskList(), false);
        }
    }

    /**
     * Processes commands until input ends or an exit command is received.
     *
     * @param ui UI used to read commands and display responses.
     * @param parser Parser used to convert input into commands.
     * @param storage Storage used to persist active task changes.
     * @param archiveStorage Storage used to persist archived task changes.
     * @param session Current active tasks and persistence state.
     * @param archiveSession Current archived tasks and persistence state.
     */
    private static void processCommands(Ui ui, Parser parser, Storage storage, Storage archiveStorage,
            SessionState session, SessionState archiveSession) {
        TaskList tasks = session.tasks();
        TaskList archivedTasks = archiveSession.tasks();
        boolean canSaveTasks = session.canSaveTasks();
        boolean canSaveArchivedTasks = archiveSession.canSaveTasks();
        // The command loop must never start without a task list to operate on.
        assert tasks != null : "Tasks must be initialized before processing commands.";

        while (ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            try {
                // Ui.readCommand() is expected to return a line whenever input is available.
                assert fullCommand != null : "An available command must not be null.";
                Command command = parser.parse(fullCommand);
                // Parser.parse() either returns a command or throws FridayException.
                assert command != null : "A valid input must produce a command.";
                Storage availableStorage = canSaveTasks ? storage : null;
                Storage availableArchiveStorage = canSaveArchivedTasks ? archiveStorage : null;
                command.execute(tasks, archivedTasks, ui, availableStorage, availableArchiveStorage);
                if (command.changesTasks()) {
                    saveTasksSafely(ui, storage, tasks, canSaveTasks);
                }
                if (command.isExit()) {
                    break;
                }
            } catch (FridayException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Stores the state needed while a Friday session is running.
     *
     * @param tasks Current task list.
     * @param canSaveTasks Whether task changes can be persisted safely.
     */
    private record SessionState(TaskList tasks, boolean canSaveTasks) {
    }

    /**
     * Attempts to save the task list and reports a friendly error if writing
     * fails, allowing the chatbot to continue running with its in-memory list.
     *
     * @param ui UI used to report save failures.
     * @param storage Storage used to persist tasks.
     * @param tasks The complete task list to save.
     * @param canSaveTasks Whether loading succeeded and saving is safe for this session.
     */
    private static void saveTasksSafely(Ui ui, Storage storage, TaskList tasks, boolean canSaveTasks) {
        assert ui != null : "UI must be available when reporting save failures.";
        assert storage != null : "Storage must be available when saving tasks.";
        assert tasks != null : "Tasks must be available when saving.";

        if (!canSaveTasks) {
            ui.showError("I couldn't save your tasks because the existing data file could not be read.");
            return;
        }

        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showError("I couldn't save your tasks. Your latest changes might not be available next time.");
        }
    }

}
