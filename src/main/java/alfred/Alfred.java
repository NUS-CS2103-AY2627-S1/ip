package alfred;

import alfred.command.Command;
import alfred.exception.AlfredException;
import alfred.parser.Parser;
import alfred.storage.Storage;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Coordinates Alfred's user interface, task storage, and command execution.
 */
public class Alfred {
    private final Storage storage;
    private final Parser parser;
    private final Ui ui;
    private final StringBuilder responseBuffer;
    private TaskList tasks;
    private String storageLoadError;
    private String lastCommandType;
    private boolean isExitRequested;

    /** Creates Alfred's collaborators. */
    public Alfred() {
        this(new Storage(), new Ui(), null);
    }

    /**
     * Creates an Alfred instance with dependencies supplied by the caller.
     *
     * @param storage task storage used to persist tasks.
     * @param ui user interface used to display responses.
     * @param responseBuffer response buffer used by the GUI, or {@code null} for the console.
     */
    Alfred(Storage storage, Ui ui, StringBuilder responseBuffer) {
        this.storage = storage;
        parser = new Parser();
        this.ui = ui;
        this.responseBuffer = responseBuffer;
    }

    /**
     * Creates an Alfred instance whose responses can be displayed in the GUI.
     *
     * @return Alfred instance configured for GUI responses.
     */
    public static Alfred createGuiAlfred() {
        StringBuilder responseBuffer = new StringBuilder();
        return new Alfred(new Storage(), new Ui(responseBuffer), responseBuffer);
    }

    /**
     * Generates Alfred's response to a GUI message.
     *
     * @param input message received from the user.
     * @return response displayed in the GUI.
     */
    public String getResponse(String input) {
        if (responseBuffer == null) {
            throw new IllegalStateException("Console Alfred cannot generate GUI responses.");
        }

        responseBuffer.setLength(0);
        isExitRequested = false;
        if (!ensureTasksLoaded()) {
            lastCommandType = "Error";
            ui.showError(storageLoadError);
            return responseBuffer.toString().stripTrailing();
        }
        try {
            Command command = executeCommand(input);
            lastCommandType = command.getClass().getSimpleName();
            isExitRequested = command.isExit();
        } catch (AlfredException e) {
            lastCommandType = "Error";
            ui.showError(e.getMessage());
        }
        return responseBuffer.toString().stripTrailing();
    }

    /**
     * Returns the type of command that produced the latest GUI response.
     *
     * @return latest command type, or {@code Error} after rejected input.
     */
    public String getLastCommandType() {
        return lastCommandType;
    }

    /**
     * Returns whether the latest GUI command requested that Alfred exit.
     *
     * @return true when the latest command is a valid exit command.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Greets the user, loads saved tasks, and executes commands until the user exits.
     */
    public void run() {
        ui.showWelcome();
        tasks = loadTasks();
        if (tasks == null) {
            ui.showError(storageLoadError);
            return;
        }

        boolean isExit = false;
        while (!isExit) {
            String commandInput = ui.readCommand();
            ui.showSeparator();

            try {
                Command command = executeCommand(commandInput);
                isExit = command.isExit();
            } catch (AlfredException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showSeparator();
            }
        }
    }

    /** Parses and executes a command using the loaded task list. */
    private Command executeCommand(String input) throws AlfredException {
        assert tasks != null : "Tasks must be loaded before executing a command";
        Command command = parser.parseCommand(input, tasks.size());
        command.execute(tasks, ui, storage);
        return command;
    }

    /**
     * Loads the saved tasks, recording the error when the saved data cannot be trusted.
     *
     * @return loaded tasks, or {@code null} after a loading error
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (AlfredException e) {
            storageLoadError = e.getMessage();
            return null;
        }
    }

    /** Loads saved tasks before the GUI processes its first command. */
    private boolean ensureTasksLoaded() {
        if (tasks == null && storageLoadError == null) {
            tasks = loadTasks();
        }
        return tasks != null;
    }

    /**
     * Starts an Alfred session.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Alfred().run();
    }

}
