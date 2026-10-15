package larry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

import larry.command.Command;
import larry.exception.LarryException;
import larry.parser.Parser;
import larry.storage.Storage;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Runs the Larry chatbot application.
 */
public class Larry {
    private static final Path DATA_FILE_PATH = Path.of("data", "larry.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private String commandType;

    /**
     * Creates Larry using the default task data path.
     */
    public Larry() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Larry with storage at the specified path and loads the saved tasks.
     *
     * @param dataFilePath Path of the task data file.
     */
    public Larry(Path dataFilePath) {
        this.ui = new Ui();
        this.storage = new Storage(dataFilePath);
        this.tasks = loadTasks();
        this.commandType = "";
    }

    /**
     * Creates and runs Larry using the default task data path.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Larry().run();
    }

    /**
     * Executes one command and returns the response for a graphical interface.
     *
     * @param input Command entered by the user.
     * @return Larry's response without console separators.
     */
    public String getResponse(String input) {
        ArrayList<String> responseLines = new ArrayList<>();
        Ui responseUi = new Ui(responseLines::add, responseLines::add);

        try {
            Command command = Parser.parseCommand(input);
            command.execute(tasks, responseUi, storage);
            commandType = command.getClass().getSimpleName();
        } catch (LarryException e) {
            commandType = "Error";
            responseUi.showError(e.getMessage());
        }

        assert !responseLines.isEmpty() : "Every command must produce a GUI response";
        // Bubble padding provides indentation, including for lines that wrap on screen.
        return responseLines.stream()
                .flatMap(String::lines)
                .map(String::stripLeading)
                .collect(Collectors.joining(System.lineSeparator())).strip();
    }

    /**
     * Returns the type of the most recently processed command for response styling.
     *
     * @return Simple command class name, or {@code Error} after invalid input.
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Responds to commands until the user enters {@code bye} or closes the input.
     */
    public void run() {
        boolean isExit = false;
        try {
            ui.showWelcome();
            while (!isExit && ui.hasNextCommand()) {
                String fullCommand = ui.readCommand();
                ui.showSeparator();

                try {
                    Command command = Parser.parseCommand(fullCommand);
                    command.execute(tasks, ui, storage);
                    isExit = command.isExit();
                } catch (LarryException e) {
                    ui.showError(e.getMessage());
                } finally {
                    ui.showSeparator();
                }
            }
        } finally {
            ui.close();
        }
    }

    /**
     * Loads saved tasks without preventing Larry from starting after a read failure.
     *
     * @return Loaded tasks, or an empty task list when the data file cannot be read.
     */
    private TaskList loadTasks() {
        try {
            TaskList loadedTasks = storage.loadTasks();
            for (String warning : storage.getLoadWarnings()) {
                ui.showWarning(warning);
            }
            return loadedTasks;
        } catch (IOException | SecurityException e) {
            ui.showLoadingError(e.getMessage());
            return new TaskList();
        }
    }
}
