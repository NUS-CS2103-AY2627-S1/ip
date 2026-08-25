package matthew;

import matthew.command.Command;
import matthew.exception.MatthewException;
import matthew.parser.Parser;
import matthew.storage.Storage;
import matthew.task.TaskList;
import matthew.ui.Ui;

/** Runs Matthew's command-line task management application. */
public class Matthew {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates an application that stores tasks at the specified file path.
     *
     * @param filePath File used to load and save tasks.
     */
    public Matthew(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks;

        try {
            loadedTasks = new TaskList(storage.load());
        } catch (MatthewException e) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
    }

    /** Starts the command loop and processes commands until the user exits. */
    public void run() {
        ui.showWelcome();

        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();

                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();

            } catch (MatthewException e) {
                ui.showError(e.getMessage());

            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts Matthew using the default task storage file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String... args) {
        new Matthew("data/matthew.txt").run();
    }
}
