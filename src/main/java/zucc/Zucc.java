package zucc;

import java.nio.file.Path;

import zucc.command.Command;
import zucc.storage.Storage;
import zucc.task.TaskList;
import zucc.ui.TextUi;
import zucc.ui.Ui;

/**
 * Coordinates Zucc's task state, persistence, parsing, and user interface.
 */
public class Zucc {
    /** File used to preserve tasks between application runs. */
    private static final Path TASK_FILE_PATH = Path.of("data", "zucc.txt");

    /** Tasks in the current chatbot session. */
    private final TaskList tasks;

    /** Persistent storage updated whenever the task state changes. */
    private final Storage storage;

    /**
     * Creates a chatbot backed by the default task file.
     *
     * @throws ZuccException if existing task data cannot be loaded.
     */
    public Zucc() throws ZuccException {
        this(TASK_FILE_PATH);
    }

    /**
     * Creates a chatbot whose state is backed by the given data file.
     *
     * @param taskFilePath file from which tasks are loaded and to which they are saved.
     * @throws ZuccException if existing task data cannot be loaded.
     */
    public Zucc(Path taskFilePath) throws ZuccException {
        storage = new Storage(taskFilePath);
        tasks = new TaskList(storage.loadTasks());
    }

    /**
     * Greets a terminal user and handles commands until input ends or the user enters {@code bye}.
     *
     * @param ui terminal user interface for this interactive session.
     */
    public void run(TextUi ui) {
        ui.showGreeting();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            isExit = executeCommand(ui.readCommand(), ui);
        }
    }

    /**
     * Parses and executes one command for an arbitrary user interface.
     *
     * @param input complete command entered by the user.
     * @param ui user interface through which responses are shown.
     * @return {@code true} if the command ends the current session.
     */
    public boolean executeCommand(String input, Ui ui) {
        try {
            Command command = Command.parse(input);
            command.execute(tasks, ui, storage);
            return command.isExit();
        } catch (ZuccException exception) {
            ui.showError(exception.getMessage());
            return false;
        }
    }

    /**
     * Starts the optional terminal interface.
     *
     * @param args command-line arguments; not used by this application.
     */
    public static void main(String[] args) {
        try (TextUi ui = new TextUi()) {
            try {
                new Zucc().run(ui);
            } catch (ZuccException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }
}
