package nova;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import nova.command.Command;
import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Coordinates Nova's storage, task list, parser, and user interface.
 */
public class Nova {
    private static final String WELCOME_MESSAGE = "Nova online! Your mission navigator is ready.\n"
            + "What shall we launch today?";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final NovaException loadingError;

    /**
     * Creates Nova and loads its saved tasks.
     * A loading error is retained so it can be shown after the welcome message.
     *
     * @param filePath path of Nova's task data file.
     */
    public Nova(String filePath) {
        storage = new Storage(Path.of(filePath));
        ui = new Ui();
        parser = new Parser();

        TaskList loadedTasks;
        NovaException startupError = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (NovaException e) {
            loadedTasks = new TaskList();
            startupError = e;
        }
        tasks = loadedTasks;
        loadingError = startupError;
    }

    /**
     * Runs Nova's command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError);
            ui.showSeparator();
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.showSeparator();

            try {
                isExit = executeCommand(fullCommand, ui);
            } catch (NovaException e) {
                ui.showError(e);
            } finally {
                ui.showSeparator();
            }
        }
    }

    /**
     * Executes one command and returns text suitable for a graphical chat dialog.
     *
     * @param fullCommand complete command entered by the user.
     * @return the chatbot's response and whether the command exits Nova.
     */
    public Response getResponse(String fullCommand) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        boolean isExit = false;
        boolean isError = false;

        try (PrintStream responseOutput = new PrintStream(responseBytes, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(responseOutput);
            try {
                isExit = executeCommand(fullCommand.trim(), responseUi);
            } catch (NovaException e) {
                isError = true;
                responseUi.showError(e);
            }
        }

        String message = responseBytes.toString(StandardCharsets.UTF_8).strip();
        return new Response(message, isExit, isError);
    }

    /**
     * Returns the graphical interface's greeting, including any storage loading warning.
     *
     * @return welcome text to display when the graphical interface opens.
     */
    public String getWelcomeMessage() {
        if (loadingError == null) {
            return WELCOME_MESSAGE;
        }
        return WELCOME_MESSAGE + "\n\nNAVIGATION ALERT: " + loadingError.getMessage();
    }

    /**
     * Parses and executes one command using the supplied user interface.
     *
     * @param fullCommand complete command entered by the user.
     * @param commandUi user interface that receives the command result.
     * @return whether the command exits Nova.
     * @throws NovaException if the command cannot be parsed or executed.
     */
    private boolean executeCommand(String fullCommand, Ui commandUi) throws NovaException {
        Command command = parser.parse(fullCommand);
        assert command != null : "Parser must return a command for valid input";
        command.execute(tasks, commandUi, storage);
        return command.isExit();
    }

    /**
     * Starts Nova using its default relative data-file path.
     *
     * @param args command-line arguments, which Nova does not currently use.
     */
    public static void main(String[] args) {
        new Nova("data/nova.txt").run();
    }

    /**
     * Contains the result of executing one command from the graphical interface.
     *
     * @param message chatbot response to display.
     * @param isExit whether Nova should stop accepting commands.
     * @param isError whether the command produced an error.
     */
    public record Response(String message, boolean isExit, boolean isError) {
    }
}
