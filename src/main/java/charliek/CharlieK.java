package charliek;

import java.nio.file.Path;

import charliek.command.Command;
import charliek.exception.CharlieKException;
import charliek.exception.TaskStorageException;
import charliek.model.SampleData;
import charliek.model.TaskList;
import charliek.parser.Parser;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Runs the CharlieK command-line chatbot.
 */
public class CharlieK {
    /**
     * Stores the tasks entered during this run of the program.
     */
    private final TaskList tasks;

    /**
     * Provides the task list's file-system persistence.
     */
    private final Storage storage;

    /**
     * Interprets command lines entered by the user.
     */
    private final Parser parser;

    /**
     * Handles console input and common session messages.
     */
    private final Ui ui;

    /**
     * Creates an application instance using the supplied task-file path.
     *
     * @param filePath the path to the file used for task persistence.
     */
    public CharlieK(String filePath) {
        storage = new Storage(Path.of(filePath));
        tasks = new TaskList();
        ui = new Ui();
        parser = new Parser(tasks, ui, storage);
    }

    /**
     * Starts the application session.
     */
    public void run() {
        String loadingError = null;
        try {
            loadTasks();
        } catch (TaskStorageException exception) {
            loadingError = exception.getMessage();
        } catch (RuntimeException exception) {
            loadingError = "I couldn't load saved tasks because the saved data is invalid.";
        }

        ui.showWelcome(loadingError);

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            try {
                Command executableCommand = parser.parse(command);
                executableCommand.execute();
                if (executableCommand.isExit()) {
                    break;
                }
            } catch (CharlieKException exception) {
                ui.showError(exception.getMessage());
            } catch (RuntimeException exception) {
                ui.showProcessingError();
            }
            ui.showLine();
        }
    }

    /**
     * Starts CharlieK using the default task-file location.
     *
     * @param args command-line arguments, which are currently ignored.
     */
    public static void main(String... args) {
        new CharlieK("data/charliek.csv").run();
    }

    /**
     * Loads task lines saved by the storage component when the application starts.
     * Missing files are initialized with starter tasks for new users.
     */
    private void loadTasks() throws TaskStorageException {
        tasks.replaceWith(storage.loadOrCreate(SampleData.create()));
    }
}
