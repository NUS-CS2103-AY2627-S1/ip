package rudra;

import java.util.ArrayList;

import rudra.command.Command;
import rudra.exception.RudraException;
import rudra.parser.Parser;
import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Rudra is a simple chatbot that echoes user commands until asked to exit.
 */
public class Rudra {
    private static final String DATA_FILE_PATH = "data/rudra.txt";

    /**
     * Starts the chatbot and handles user input until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage(DATA_FILE_PATH);
        ArrayList<Task> tasks = loadTasksAtStartup(storage, ui);

        boolean isExit = false;
        while (ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            try {
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (RudraException e) {
                ui.showError(e.getMessage());
            }

            if (isExit) {
                break;
            }
        }
    }

    /**
     * Loads tasks from disk when the chatbot starts and reports any recoverable storage issues.
     *
     * @param storage Storage helper used to read saved tasks.
     * @param ui UI helper used to print startup messages.
     * @return Task list to use for this session.
     */
    private static ArrayList<Task> loadTasksAtStartup(Storage storage, Ui ui) {
        try {
            Storage.LoadResult loadResult = storage.loadTasks();
            if (loadResult.getSkippedTaskCount() > 0) {
                ui.showCorruptedTaskWarning(loadResult.getSkippedTaskCount());
            }
            return loadResult.getTasks();
        } catch (RudraException e) {
            ui.showLoadingError(e.getMessage());
            return new ArrayList<>();
        }
    }
}
