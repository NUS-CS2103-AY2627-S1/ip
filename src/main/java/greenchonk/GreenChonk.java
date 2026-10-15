package greenchonk;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import greenchonk.command.Command;
import greenchonk.exception.GreenChonkException;
import greenchonk.parser.Parser;
import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Greets the user as Green Chonk, manages tasks, and exits on {@code bye}.
 */
public class GreenChonk {
    private static final String DATA_FILE_PATH = "data/greenchonk.txt";

    private final Storage storage;
    private final Ui ui;
    private TaskList guiTasks;

    /**
     * Creates Green Chonk with its default data file.
     */
    public GreenChonk() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Green Chonk with a command-line UI and file-backed storage.
     *
     * @param filePath the path of the task data file.
     */
    public GreenChonk(String filePath) {
        this(new Storage(filePath), new Ui());
    }

    /**
     * Creates Green Chonk with supplied collaborators for automated testing.
     *
     * @param storage the persistence component to use.
     * @param ui the command-line interface to use.
     */
    GreenChonk(Storage storage, Ui ui) {
        this.storage = storage;
        this.ui = ui;
    }

    /**
     * Starts Green Chonk with its default data file.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new GreenChonk().run();
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        TaskList tasks = loadTasks(ui);
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            isExit = executeCommand(ui.readCommand(), tasks, ui);
        }
    }

    /**
     * Executes one command and returns its user-facing response for the GUI.
     *
     * @param input the command entered by the user.
     * @return the response produced while executing the command.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        try (PrintStream responseStream = new PrintStream(outputBuffer, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(InputStream.nullInputStream(), responseStream);
            if (guiTasks == null) {
                guiTasks = loadTasks(responseUi);
            }

            executeCommand(input, guiTasks, responseUi);
        }
        return outputBuffer.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Executes one command and reports expected failures through the target UI.
     *
     * @param input the command entered by the user.
     * @param tasks the task list on which to execute the command.
     * @param targetUi the UI through which results are shown.
     * @return true if the command ends the application, or false otherwise.
     */
    private boolean executeCommand(String input, TaskList tasks, Ui targetUi) {
        try {
            Command command = Parser.parse(input.strip());
            command.execute(tasks, targetUi, storage);
            return command.isExit();
        } catch (GreenChonkException exception) {
            targetUi.showError(exception.getMessage());
            return false;
        }
    }

    /**
     * Loads saved tasks, creating the data directory and file on first use.
     *
     * @return the tasks restored from disk, or an empty task list if loading fails.
     */
    private TaskList loadTasks(Ui targetUi) {
        try {
            return new TaskList(storage.load());
        } catch (GreenChonkException exception) {
            targetUi.showLoadingError(exception.getMessage());
            return new TaskList();
        }
    }
}
