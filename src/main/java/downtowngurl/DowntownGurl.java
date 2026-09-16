package downtowngurl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import downtowngurl.command.Command;
import downtowngurl.exception.DowntownGurlException;
import downtowngurl.parser.Parser;
import downtowngurl.storage.Storage;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Entry point for the Downtown Gurl chatbot application.
 */
public class DowntownGurl {
    private static final Path TASK_FILE_PATH = Path.of("data", "downtownGurl.txt");
    private static final String LOAD_ERROR_MESSAGE = "Oops, I couldn't load your tasks from disk.";
    private static final String CORRUPTED_LINE_MESSAGE = "I skipped a corrupted saved task on line ";

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private boolean hasLoadedTasks;
    private boolean isExit;

    /**
     * Creates the chatbot application using the default file for saved tasks.
     */
    public DowntownGurl() {
        this(TASK_FILE_PATH);
    }

    /**
     * Creates the chatbot application using the given file for saved tasks.
     *
     * @param taskFilePath Path to the file used to persist tasks.
     */
    public DowntownGurl(Path taskFilePath) {
        this.ui = new Ui();
        this.storage = new Storage(taskFilePath);
        this.tasks = new TaskList();
        this.hasLoadedTasks = false;
        this.isExit = false;
    }

    /**
     * Starts reading and handling user commands.
     */
    public void run() {
        this.ui.showWelcome();
        ensureTasksLoaded(this.ui);
        boolean isExit = false;
        while (!isExit && this.ui.hasNextCommand()) {
            try {
                String fullCommand = this.ui.readCommand();
                this.ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(this.tasks, this.storage, this.ui);
                isExit = command.isExit();
            } catch (DowntownGurlException e) {
                this.ui.showError(e.getMessage());
            } finally {
                this.ui.showLine();
            }
        }
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input user message from the chat window.
     * @return response to display in the chat window.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream responseOutput = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
        Ui responseUi = new Ui(responseOutput);

        ensureTasksLoaded(responseUi);
        try {
            Command command = Parser.parse(input);
            command.execute(this.tasks, this.storage, responseUi);
            this.isExit = command.isExit();
        } catch (DowntownGurlException e) {
            responseUi.showError(e.getMessage());
        }
        return outputStream.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Returns whether the most recent command should exit the application.
     *
     * @return true if the application should exit, false otherwise.
     */
    public boolean isExit() {
        return this.isExit;
    }

    /**
     * Loads tasks from the data file if it already exists.
     *
     * @return Task list from the data file, or an empty list if the file does not exist.
     */
    private TaskList loadTasks() {
        return loadTasks(this.ui);
    }

    /**
     * Loads tasks from the data file if it already exists.
     *
     * @param ui UI helper used to show loading errors.
     * @return Task list from the data file, or an empty list if the file does not exist.
     */
    private TaskList loadTasks(Ui ui) {
        try {
            TaskList loadedTasks = new TaskList(this.storage.loadTasks());
            for (int lineNumber : this.storage.getCorruptedLineNumbers()) {
                ui.showError(CORRUPTED_LINE_MESSAGE + lineNumber + ".");
            }
            return loadedTasks;
        } catch (DowntownGurlException e) {
            ui.showError(LOAD_ERROR_MESSAGE);
            return new TaskList();
        }
    }

    /**
     * Loads saved tasks once before the first command is handled.
     *
     * @param ui UI helper used to show loading errors.
     */
    private void ensureTasksLoaded(Ui ui) {
        if (this.hasLoadedTasks) {
            return;
        }
        this.tasks = loadTasks(ui);
        this.hasLoadedTasks = true;
    }

    /**
     * Runs the chatbot using the default task file path.
     *
     * @param args Command-line arguments, currently unused.
     */
    public static void main(String[] args) {
        new DowntownGurl(TASK_FILE_PATH).run();
    }
}
