package avon;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import avon.command.Command;
import avon.exception.AvonException;
import avon.exception.StorageException;
import avon.parser.Parser;
import avon.storage.Storage;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Runs the command-line interface for Avon.
 */
public class Avon {
    private static final Path DATA_FILE_PATH = Path.of("data", "avon.txt");

    private final Ui ui;
    private final Storage storage;

    /**
     * Creates Avon with its standard console and data file dependencies.
     */
    public Avon() {
        this(new Ui(), new Storage(DATA_FILE_PATH));
    }

    /**
     * Creates Avon with the specified interface and storage dependencies.
     *
     * @param ui the command-line interface.
     * @param storage the persistent task storage.
     */
    public Avon(Ui ui, Storage storage) {
        this.ui = ui;
        this.storage = storage;
    }

    /**
     * Starts Avon and processes commands until the user exits or input ends.
     *
     * @param args command-line arguments, which Avon does not use.
     */
    public static void main(String[] args) {
        new Avon().run();
    }

    /**
     * Processes commands until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        TaskList taskList;
        try {
            taskList = loadTasks(storage);
        } catch (StorageException exception) {
            ui.showError(exception);
            ui.close();
            return;
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.showSeparator();

            try {
                Command command = parseAndExecuteCommand(fullCommand, taskList, ui);
                isExit = command.isExit();
            } catch (AvonException exception) {
                ui.showError(exception);
            }
            ui.showSeparator();
        }
        ui.close();
    }

    /**
     * Executes one GUI command and returns Avon's complete response.
     *
     * @param input the command entered in the GUI.
     * @return the response to display in Avon's dialog box.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        Ui responseUi = new Ui(InputStream.nullInputStream(), new PrintStream(responseBytes));
        try {
            TaskList taskList = loadTasks(storage);
            parseAndExecuteCommand(input, taskList, responseUi);
        } catch (AvonException exception) {
            responseUi.showError(exception);
        } finally {
            responseUi.close();
        }
        return responseBytes.toString().stripTrailing();
    }

    /**
     * Parses and executes one command against the supplied task list and UI.
     *
     * @param fullCommand the complete command to execute.
     * @param taskList the tasks affected by the command.
     * @param commandUi the interface that receives the command response.
     * @return the command that is parsed and executed.
     */
    private Command parseAndExecuteCommand(
            String fullCommand, TaskList taskList, Ui commandUi) throws AvonException {
        Command command = Parser.parse(fullCommand);
        command.execute(taskList, commandUi, storage);
        return command;
    }

    /**
     * Loads saved tasks before command processing begins.
     *
     * @param storage the persistent task storage.
     * @return the restored task list.
     * @throws StorageException if the saved tasks cannot be restored safely.
     */
    private static TaskList loadTasks(Storage storage) throws StorageException {
        return new TaskList(storage.load());
    }

}
