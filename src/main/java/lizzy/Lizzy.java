package lizzy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

import lizzy.command.Command;
import lizzy.exception.LizzyException;
import lizzy.parser.Parser;
import lizzy.storage.Storage;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * The entry point and application coordinator for the Lizzy chatbot.
 */
public class Lizzy {
    /**
     * Contains the text and presentation category for one graphical-interface response.
     *
     * @param text the response text to display
     * @param isError whether the response explains an invalid command
     * @param isExit whether the response ends the current session
     */
    public record Response(String text, boolean isError, boolean isExit) {
    }

    /**
     * Describes how executing a command affects its session and presentation.
     *
     * @param isExit whether the command ends a console session
     * @param isError whether executing the command produced an error
     */
    private record CommandStatus(boolean isExit, boolean isError) {
    }

    /**
     * The path used for persistent task data unless the caller overrides it with a system property.
     */
    private static final Path DATA_FILE_PATH = Path.of(
            System.getProperty("lizzy.data.path", "data/lizzy.txt"));
    /**
     * The component responsible for all console interaction.
     */
    private final Ui ui;
    /**
     * Persists task changes for both the console and graphical interfaces.
     */
    private final Storage storage;
    /**
     * Holds the task state shared by commands in the current session.
     */
    private final TaskList tasks;
    /**
     * Describes a storage load failure that should be shown when a session starts.
     */
    private final String startupError;

    /**
     * Creates Lizzy with its standard console UI.
     */
    public Lizzy() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Lizzy using the specified task data file.
     *
     * @param dataFilePath the path used to load and save tasks
     */
    Lizzy(Path dataFilePath) {
        ui = new Ui();
        storage = new Storage(dataFilePath);

        TaskList loadedTasks;
        String loadError = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (LizzyException exception) {
            loadedTasks = new TaskList();
            loadError = exception.getMessage();
        }
        tasks = loadedTasks;
        startupError = loadError;
    }

    /**
     * Runs Lizzy until the user enters {@code bye} or closes standard input.
     */
    public void run() {
        ui.showWelcome();
        if (startupError != null) {
            ui.showError(startupError);
            ui.showDivider();
        }

        boolean isExit = false;
        while (ui.hasNextCommand() && !isExit) {
            String command = ui.readCommand();
            ui.showDivider();
            isExit = executeCommand(command, ui).isExit();
            ui.showDivider();
        }
    }

    /**
     * Executes one command and returns the text Lizzy would display for it.
     *
     * @param input the command entered through the graphical interface
     * @return Lizzy's response without console divider lines, using LF line separators
     */
    public String getResponse(String input) {
        return getResponseWithStatus(input).text();
    }

    /**
     * Returns the recoverable storage error encountered while loading saved tasks, if any.
     *
     * @return the startup error, or an empty value when task data loaded successfully
     */
    public Optional<String> getStartupError() {
        return Optional.ofNullable(startupError);
    }

    /**
     * Executes one command and returns both its text and whether it is an error response.
     *
     * @param input the command entered through the graphical interface
     * @return the response text and its presentation category
     */
    public Response getResponseWithStatus(String input) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        CommandStatus commandStatus;
        try (PrintStream responseStream = new PrintStream(responseBytes, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(InputStream.nullInputStream(), responseStream);
            commandStatus = executeCommand(input, responseUi);
        }
        String response = responseBytes.toString(StandardCharsets.UTF_8);
        return new Response(response.replace("\r\n", "\n").stripTrailing(),
                commandStatus.isError(), commandStatus.isExit());
    }

    /**
     * Parses and executes one command, reporting validation errors through the supplied UI.
     *
     * @param input the complete command text
     * @param targetUi the interface that receives command output
     * @return the command's session and presentation status
     */
    private CommandStatus executeCommand(String input, Ui targetUi) {
        try {
            Command parsedCommand = Parser.parse(input);
            parsedCommand.execute(tasks, targetUi, storage);
            return new CommandStatus(parsedCommand.isExit(), false);
        } catch (LizzyException exception) {
            targetUi.showError(exception.getMessage());
            return new CommandStatus(false, true);
        }
    }

    /**
     * Starts the Lizzy application.
     *
     * @param args command-line arguments, which Lizzy does not use
     */
    public static void main(String[] args) {
        new Lizzy().run();
    }

}
