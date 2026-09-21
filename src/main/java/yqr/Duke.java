package yqr;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import yqr.command.Command;
import yqr.exception.YqrException;
import yqr.parser.Parser;
import yqr.storage.Storage;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Coordinates the yqr chatbot's user interface, task list, parser, and storage.
 */
public class Duke {
    private static final String DEFAULT_FILE_PATH = Path.of("data", "yqr.txt").toString();
    private static final String WELCOME_MESSAGE = "Hello! I'm yqr.\nWhat can I do for you?";

    private final Storage storage;
    private final Ui ui;
    private TaskList taskList;
    private boolean hasExited;
    private String loadingMessage = "";

    /**
     * Creates a chatbot that uses the default storage file.
     */
    public Duke() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a chatbot that saves its tasks at the given file path.
     *
     * @param filePath path of the task data file.
     */
    public Duke(String filePath) {
        storage = new Storage(Path.of(filePath));
        ui = new Ui();
        loadTasks();
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     */
    public void run() {
        ui.showWelcome();

        while (!hasExited && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                executeCommand(fullCommand, ui);
            } catch (YqrException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts the chatbot using its default storage file.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Duke().run();
    }

    /**
     * Executes one user command and returns the lines displayed by the chatbot.
     *
     * @param input command entered by the user.
     * @return chatbot response, with multiple lines joined by newline characters.
     */
    public String getResponse(String input) {
        return getCommandResult(input).text();
    }

    /**
     * Executes one user command and returns its display text and outcome.
     *
     * @param input command entered by the user.
     * @return result containing the response text and error status.
     */
    public CommandResult getCommandResult(String input) {
        if (hasExited) {
            return new CommandResult(
                    "This session has ended. Restart yqr to enter more commands.", true);
        }

        List<String> responseLines = new ArrayList<>();
        Ui responseUi = new Ui(responseLines::add);
        boolean isError = false;
        try {
            executeCommand(input, responseUi);
        } catch (YqrException e) {
            responseUi.showError(e.getMessage());
            isError = true;
        }
        return new CommandResult(String.join("\n", responseLines), isError);
    }

    /**
     * Returns the greeting shown when the GUI opens.
     *
     * @return greeting, preceded by a loading warning when saved tasks could not be loaded.
     */
    public String getWelcomeMessage() {
        if (loadingMessage.isEmpty()) {
            return WELCOME_MESSAGE;
        }
        return loadingMessage + "\n\n" + WELCOME_MESSAGE;
    }

    /**
     * Returns whether startup encountered a problem while loading saved tasks.
     *
     * @return {@code true} when the GUI should highlight the loading warning.
     */
    public boolean hasLoadingError() {
        return !loadingMessage.isEmpty();
    }

    /**
     * Returns whether the user has entered the exit command.
     *
     * @return {@code true} after a successful {@code bye} command.
     */
    public boolean hasExited() {
        return hasExited;
    }

    /**
     * Parses and executes one command, then records whether it ends the session.
     *
     * @param input command entered by the user.
     * @param commandUi user interface used to display the command result.
     * @throws YqrException if the command cannot be parsed or executed.
     */
    private void executeCommand(String input, Ui commandUi) throws YqrException {
        Command command = Parser.parse(input);
        command.execute(taskList, commandUi, storage);
        hasExited = command.isExit();
    }

    /**
     * Loads saved tasks, falling back to an empty task list when loading fails.
     */
    private void loadTasks() {
        try {
            taskList = storage.loadTasks();
        } catch (YqrException e) {
            ui.showLoadingError(e.getMessage());
            loadingMessage = e.getMessage() + "\nStarting with an empty task list instead.";
            taskList = new TaskList();
        }
    }
}
