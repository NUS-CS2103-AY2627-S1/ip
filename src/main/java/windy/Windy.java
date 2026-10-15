package windy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import windy.command.Command;
import windy.command.CommandContext;
import windy.command.Parser;
import windy.command.UndoHistory;
import windy.exception.InvalidInputFormatException;
import windy.storage.Storage;
import windy.task.Task;
import windy.task.TaskList;
import windy.ui.Ui;

/**
 * The main class for the Windy chatbot application.
 */
public class Windy {

    private static final String DEFAULT_FILE_PATH = "data/windy.txt";

    private final TaskList tasks;
    private final Storage storage;
    private final Ui ui;
    private final UndoHistory history = new UndoHistory();
    private boolean isExitRequested;

    /**
     * Creates the application and restores tasks from the default data file.
     */
    public Windy() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates the application and restores tasks from the specified data file.
     *
     * @param filePath path of the task data file.
     */
    Windy(String filePath) {
        List<Task> loadedTasks;
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            loadedTasks = storage.loadTasks();
        } catch (IOException exception) {
            ui.showError("Could not load tasks from '" + filePath + "': " + exception.getMessage()
                    + ". No saved tasks were loaded; changes may overwrite the file.");
            loadedTasks = new ArrayList<>();
        }
        tasks = new TaskList(loadedTasks);
    }

    /**
     * Returns the welcome output used by the console interface.
     *
     * @return Windy's welcome message.
     */
    public String getWelcomeMessage() {
        return captureUiOutput(Ui::showWelcome);
    }

    /**
     * Executes a command and returns the response for display in the GUI.
     *
     * @param input command entered by the user.
     * @return Windy's response to the command.
     */
    public String getResponse(String input) {
        isExitRequested = false;
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(
                responseBytes, true, StandardCharsets.UTF_8)) {
            Ui responseUi = Ui.createDialogUi(responseOutput);
            try {
                Command command = Parser.parseCommand(input.trim(), tasks.getNumTasks());
                CommandContext context = new CommandContext(tasks, responseUi, storage, history);
                command.execute(context);
                isExitRequested = context.isExitRequested();
                if (isExitRequested) {
                    responseUi.showBye();
                }
            } catch (InvalidInputFormatException exception) {
                responseUi.showError(exception.getMessage());
            }
        }
        return responseBytes.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Checks whether the latest valid command requested application exit.
     *
     * @return {@code true} if the latest command was {@code bye}.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Captures output produced by one of the console UI's display methods.
     *
     * @param displayAction display method to invoke.
     * @return captured output formatted for a dialog box.
     */
    private String captureUiOutput(Consumer<Ui> displayAction) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(
                responseBytes, true, StandardCharsets.UTF_8)) {
            displayAction.accept(Ui.createDialogUi(responseOutput));
        }
        return responseBytes.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Reads, validates, and executes commands until the user exits or input ends.
     */
    private void runCommandLoop() {
        commandLoop:
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();

            ui.showLine();
            try {
                Command command = Parser.parseCommand(input, tasks.getNumTasks());
                CommandContext context = new CommandContext(tasks, ui, storage, history);
                command.execute(context);
                if (context.isExitRequested()) {
                    break commandLoop;
                }
            } catch (InvalidInputFormatException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showLine();
        }
    }

    /**
     * Starts the Windy command-line application.
     *
     * @param args command-line arguments; currently unused.
     */
    public static void main(String[] args) {
        Windy windy = new Windy();
        windy.ui.showWelcome();
        windy.runCommandLoop();
        windy.ui.showBye();
    }
}
