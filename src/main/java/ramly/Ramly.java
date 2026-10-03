package ramly;

import java.util.ArrayList;
import java.util.List;

import ramly.command.Command;
import ramly.exception.CommandFormatException;
import ramly.exception.StorageException;
import ramly.exception.TaskValidationException;
import ramly.model.Task;
import ramly.model.TaskList;
import ramly.parser.Parser;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Main application class that coordinates the UI, parser, storage, and commands. */
public class Ramly {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final List<String> startupMessages;
    private final boolean storageAvailable;

    /** Creates an application using the specified task storage file. */
    public Ramly(String filePath) {
        this(new Storage(filePath));
    }

    /** Creates an application with injectable storage for controlled verification. */
    Ramly(Storage storage) {
        assert storage != null : "Storage must not be null";
        ui = new Ui();
        parser = new Parser();
        this.storage = storage;
        ArrayList<Task> loadedTasks;
        boolean loadedSuccessfully;
        List<String> messages;
        try {
            loadedTasks = storage.load();
            loadedSuccessfully = true;
            messages = storage.getWarnings();
        } catch (StorageException e) {
            loadedTasks = new ArrayList<>();
            loadedSuccessfully = false;
            messages = List.of(e.getMessage());
        }
        tasks = new TaskList(loadedTasks);
        storageAvailable = loadedSuccessfully;
        startupMessages = messages;
    }

    /** Runs the command-processing loop until an exit command is received. */
    public void run() {
        ui.showWelcome();
        startupMessages.forEach(ui::showError);
        if (!storageAvailable) {
            ui.close();
            return;
        }
        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            if (input == null) {
                break;
            }
            ui.showLine();
            isExit = executeCommand(input, ui);
            if (!isExit) {
                ui.showLine();
            }
        }
        ui.close();
    }

    /**
     * Parses and executes one command, writing every response through the supplied UI.
     *
     * @return true when the command requests that the application exits
     */
    public boolean executeCommand(String input, Ui outputUi) {
        assert input != null : "Command input must not be null";
        assert outputUi != null : "Output UI must not be null";
        if (!storageAvailable) {
            outputUi.showError(startupMessages.get(0));
            return false;
        }
        TaskList.State originalState = tasks.createSnapshot();
        try {
            Command command = parser.parse(input);
            assert command != null : "Parser must return a command";
            command.execute(tasks, outputUi, storage);
            return command.isExit();
        } catch (CommandFormatException | TaskValidationException e) {
            outputUi.showError(e.getMessage());
            return false;
        } catch (StorageException e) {
            tasks.restoreSnapshot(originalState);
            outputUi.showError(e.getMessage());
            return false;
        } catch (RuntimeException e) {
            outputUi.showError("I hit a rough patch and couldn't process that command. Please check its format.");
            return false;
        }
    }

    /** Returns messages generated while opening persistent storage. */
    public List<String> getStartupMessages() {
        return List.copyOf(startupMessages);
    }

    /** Returns whether commands can safely use persistent storage. */
    public boolean isStorageAvailable() {
        return storageAvailable;
    }

    /** Starts Ramly using the default task data file. */
    public static void main(String[] args) {
        new Ramly("./data/ramly.txt").run();
    }

}
