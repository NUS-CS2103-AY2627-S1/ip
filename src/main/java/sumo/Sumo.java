package sumo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import sumo.command.Command;
import sumo.exception.SumoException;
import sumo.parser.Parser;
import sumo.storage.Storage;
import sumo.task.TaskList;
import sumo.ui.Ui;

/** Coordinates Sumo's user interface, parsing, task operations, and storage. */
public class Sumo {
    private static final String DEFAULT_FILE_PATH = Path.of("data", "sumo.txt").toString();

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;
    private final List<String> startupMessages = new ArrayList<>();
    private boolean isExit;

    /** Creates a Sumo application backed by the default task file. */
    public Sumo() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a Sumo application backed by the given task file.
     *
     * @param filePath path used to load and save tasks.
     */
    public Sumo(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(Path.of(filePath));
        this.parser = new Parser();
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load(new Ui(startupMessages::add)));
        } catch (IOException exception) {
            loadedTasks = new TaskList();
            new Ui(startupMessages::add).showLoadingError(getErrorMessage(exception)
                    + " Saving is disabled; fix the data file and restart Sumo.");
        }
        this.tasks = loadedTasks;
        this.isExit = false;
    }

    /** Starts the command-reading loop. */
    public void run() {
        ui.showWelcome();
        startupMessages.forEach(System.out::println);
        while (!isExit && ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showSeparator();
            executeCommand(input, ui);
            ui.showSeparator();
        }
    }

    /**
     * Returns Sumo's response to one GUI command.
     *
     * @param input command entered by the user.
     * @return response text to display in the GUI.
     */
    public String getResponse(String input) {
        StringBuilder response = new StringBuilder();
        Ui responseUi = new Ui(line -> {
            if (!response.isEmpty()) {
                response.append(System.lineSeparator());
            }
            response.append(line);
        });
        executeCommand(input, responseUi);
        return response.toString().strip();
    }

    /**
     * Returns loading warnings for display when the GUI opens.
     *
     * @return loading warnings, or an empty string when all tasks loaded successfully.
     */
    public String getStartupMessage() {
        return String.join(System.lineSeparator(), startupMessages).strip();
    }

    /**
     * Returns whether the user has ended the current session.
     *
     * @return whether an exit command has been executed.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Starts Sumo using its default task file.
     *
     * @param args command-line arguments, which Sumo ignores.
     */
    public static void main(String[] args) {
        new Sumo().run();
    }

    /** Executes one command and sends its response to the supplied UI. */
    private void executeCommand(String input, Ui responseUi) {
        try {
            Command command = parser.parse(input, tasks.size());
            command.execute(tasks, responseUi, storage);
            isExit = command.isExit();
        } catch (SumoException exception) {
            responseUi.showCommandError(exception.getMessage());
        } catch (IOException exception) {
            responseUi.showSavingError(getErrorMessage(exception));
        }
    }

    /** Extracts a useful display message from a storage exception. */
    private String getErrorMessage(IOException exception) {
        return exception.getMessage() == null
                ? "The data file could not be accessed."
                : exception.getMessage();
    }
}
