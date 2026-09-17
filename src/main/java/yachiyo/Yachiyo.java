package yachiyo;

import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Optional;

import yachiyo.command.Command;
import yachiyo.exception.ErrorCategory;
import yachiyo.exception.YachiyoException;
import yachiyo.parser.Parser;
import yachiyo.storage.Storage;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Runs the Yachiyo task management application.
 */
public class Yachiyo {
    private static final Path DATA_FILE_PATH = Path.of("data", "yachiyo.txt");

    private TaskList tasks = new TaskList();
    private final Storage storage;
    private final Ui ui = new Ui();
    private boolean isInitialized;
    private boolean isExitRequested;
    private ErrorCategory lastErrorCategory;

    /**
     * Creates a task manager backed by the default data file.
     */
    public Yachiyo() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a task manager backed by the specified data file.
     *
     * @param dataFilePath file used to load and save tasks.
     */
    Yachiyo(Path dataFilePath) {
        this.storage = new Storage(dataFilePath);
    }

    /**
     * Starts the Yachiyo application.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Yachiyo().run();
    }

    /**
     * Processes a command and returns the response produced by the task manager.
     *
     * @param input user's message.
     * @return response produced by parsing and executing the command.
     */
    public String getResponse(String input) {
        lastErrorCategory = null;
        StringWriter responseWriter = new StringWriter();
        try (Ui responseUi = new Ui(responseWriter)) {
            isExitRequested = executeCommand(input.trim(), responseUi);
        }
        return responseWriter.toString().stripTrailing();
    }

    /**
     * Loads saved tasks for interfaces that need task information before the first command.
     *
     * @return loading error response, or an empty string when initialization succeeds.
     */
    public String initialize() {
        lastErrorCategory = null;
        StringWriter responseWriter = new StringWriter();
        try (Ui responseUi = new Ui(responseWriter)) {
            initializeTasks(responseUi);
        }
        return responseWriter.toString().stripTrailing();
    }

    /**
     * Checks whether saved tasks have been loaded successfully.
     *
     * @return true if task statistics are available.
     */
    public boolean hasLoadedTasks() {
        return isInitialized;
    }

    /**
     * Returns the total number of loaded tasks.
     *
     * @return total task count.
     */
    public int getTaskCount() {
        assert isInitialized : "Tasks must be loaded before their total can be retrieved";
        return tasks.size();
    }

    /**
     * Returns the number of loaded tasks that are not completed.
     *
     * @return incomplete task count.
     */
    public int getRemainingTaskCount() {
        assert isInitialized : "Tasks must be loaded before their remaining count can be retrieved";
        return tasks.getRemainingTaskCount();
    }

    /**
     * Returns the category of the most recent response when it represents an error.
     *
     * @return error category, or an empty value if the response was successful.
     */
    public Optional<ErrorCategory> getLastErrorCategory() {
        return Optional.ofNullable(lastErrorCategory);
    }

    /**
     * Returns whether the last processed command requested an exit.
     *
     * @return true if the last command was {@code bye}.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns Yachiyo's introductory greeting.
     *
     * @return greeting shown when the GUI opens.
     */
    public String getGreeting() {
        return Ui.getGreeting();
    }

    /**
     * Runs the command-processing loop until the user exits or input ends.
     */
    private void run() {
        try (Ui ui = this.ui) {
            ui.showIntroduction();
            if (!initializeTasks(ui)) {
                return;
            }

            boolean isExit = false;
            while (!isExit && ui.hasNextCommand()) {
                String userInput = ui.readCommand().trim();

                if (userInput.isEmpty()) {
                    continue;
                }

                ui.showCommandStart();

                isExit = executeCommand(userInput, ui);
                ui.showCommandEnd();
            }
        }
    }

    /**
     * Loads saved tasks before either interface processes its first command.
     *
     * @param outputUi interface that receives a loading error, if one occurs.
     * @return true if tasks are available and commands can be processed.
     */
    private boolean initializeTasks(Ui outputUi) {
        if (isInitialized) {
            return true;
        }

        try {
            tasks = new TaskList(storage.loadTasks());
            isInitialized = true;
            return true;
        } catch (YachiyoException e) {
            recordError(e);
            outputUi.showError(e.getMessage());
            return false;
        }
    }

    /**
     * Parses and executes one command, loading tasks first unless the command exits.
     *
     * @param input command entered by the user.
     * @param outputUi interface that receives command output.
     * @return true if the command requests that the application exit.
     */
    private boolean executeCommand(String input, Ui outputUi) {
        try {
            Command command = Parser.parse(input);
            if (!command.isExit() && !initializeTasks(outputUi)) {
                return false;
            }
            command.execute(tasks, outputUi, storage);
            return command.isExit();
        } catch (YachiyoException e) {
            recordError(e);
            outputUi.showError(e.getMessage());
            return false;
        }
    }

    /**
     * Records an error category while ensuring a system error is never masked by a later error.
     *
     * @param error error reported while processing the current response.
     */
    private void recordError(YachiyoException error) {
        if (lastErrorCategory != ErrorCategory.SYSTEM_ERROR) {
            lastErrorCategory = error.getCategory();
        }
    }
}
