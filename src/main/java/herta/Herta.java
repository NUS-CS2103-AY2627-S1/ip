package herta;

import java.util.logging.Level;
import java.util.logging.Logger;

import herta.command.Command;
import herta.exception.HertaException;
import herta.exception.UsageGuidanceException;
import herta.parser.CommandTokenizer;
import herta.parser.CommandType;
import herta.parser.Parser;
import herta.storage.PersistenceState;
import herta.storage.Storage;
import herta.storage.TaskRepository;
import herta.task.TaskList;
import herta.ui.ResponseCollector;
import herta.ui.Ui;
import herta.ui.UiOutput;

/**
 * Provides the command-line entry point for the Herta task manager.
 */
public class Herta {
    private static final String DEFAULT_DATA_FILE = "data/herta.txt";
    private static final String UNEXPECTED_NO_CHANGE_ERROR = "Something went wrong while processing "
            + "that command. No data was changed.";
    private static final String UNEXPECTED_COMMITTED_ERROR = "The change was saved, but Herta could "
            + "not display the complete response.";
    private static final String UNEXPECTED_UNKNOWN_ERROR = "Something went wrong while processing "
            + "that command. Data may have changed; check your task list before continuing.";
    private static final Logger LOGGER = Logger.getLogger(Herta.class.getName());

    private final Ui ui;
    private final Parser parser;
    private final TaskRepository repository;
    private final String loadingError;

    /** Stores a parsed command together with the type used to classify its response. */
    private record ParsedCommand(Command command, CommandType commandType) {
    }

    /**
     * Creates a Herta instance backed by the default data file.
     */
    public Herta() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates a Herta instance backed by the specified data file.
     *
     * @param filePath the path of Herta's data file.
     */
    public Herta(String filePath) {
        ui = new Ui();
        parser = new Parser();

        TaskRepository loadedRepository;
        String loadError;
        try {
            loadedRepository = TaskRepository.load(filePath);
            loadError = null;
        } catch (HertaException e) {
            loadedRepository = createFallbackRepository(filePath);
            loadError = e.getMessage();
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unable to initialize Herta storage.", e);
            loadedRepository = createUnavailableRepository();
            loadError = "Failed to load tasks: the configured data path is invalid or inaccessible.";
        }
        repository = loadedRepository;
        loadingError = loadError;
    }

    /**
     * Starts Herta and processes commands entered by the user.
     */
    public void run() {
        ui.showWelcome();

        try {
            if (loadingError != null) {
                ui.showMessage(loadingError);
                return;
            }
            runCommandLoop();
        } finally {
            ui.close();
        }
    }

    /**
     * Reads and processes commands until the user exits or input reaches EOF.
     */
    private void runCommandLoop() {
        while (true) {
            String input = ui.readCommand();
            if (input == null) {
                ui.showSeparator();
                ui.showGoodbye();
                return;
            }
            ui.showSeparator();

            ResponseCategory responseCategory = processCommand(input, ui);
            if (responseCategory == ResponseCategory.EXIT) {
                return;
            }
            ui.showSeparator();
        }
    }

    /**
     * Parses and executes one command using the supplied output interface.
     *
     * @param input the command to process.
     * @param output the output interface used to display responses.
     * @return the semantic category of the processed command response.
     */
    private ResponseCategory processCommand(String input, UiOutput output) {
        if (!isReady()) {
            showMessageSafely(output, loadingError);
            return ResponseCategory.ERROR;
        }

        repository.resetPersistenceState();
        try {
            return executeCommand(parseCommand(input), output);
        } catch (HertaException e) {
            showMessageSafely(output, e.getMessage());
            return ResponseCategory.USAGE_GUIDANCE;
        } catch (RuntimeException e) {
            logUnexpectedFailure("Unexpected command parsing failure.", e);
            showMessageSafely(output, getUnexpectedErrorMessage());
            return ResponseCategory.ERROR;
        }
    }

    /** Parses one command after normalizing its input at the application boundary. */
    private ParsedCommand parseCommand(String input) throws HertaException {
        String normalizedInput = CommandTokenizer.normalize(input);
        CommandType commandType = parser.parseCommandType(normalizedInput);
        Command command = parser.parse(normalizedInput, commandType);
        return new ParsedCommand(command, commandType);
    }

    /** Executes a parsed command and reports expected or unexpected execution failures. */
    private ResponseCategory executeCommand(ParsedCommand parsedCommand, UiOutput output) {
        try {
            parsedCommand.command().execute(repository, output);
            return ResponseCategory.fromCommandType(parsedCommand.commandType());
        } catch (UsageGuidanceException e) {
            showMessageSafely(output, e.getMessage());
            return ResponseCategory.USAGE_GUIDANCE;
        } catch (HertaException e) {
            showMessageSafely(output, e.getMessage());
            return ResponseCategory.ERROR;
        } catch (RuntimeException e) {
            logUnexpectedFailure("Unexpected command execution failure.", e);
            showMessageSafely(output, getUnexpectedErrorMessage());
            return getUnexpectedResponseCategory(parsedCommand);
        }
    }

    /** Returns the response category that remains truthful after an unexpected failure. */
    private ResponseCategory getUnexpectedResponseCategory(ParsedCommand parsedCommand) {
        return repository.getPersistenceState() == PersistenceState.COMMITTED
                ? ResponseCategory.fromCommandType(parsedCommand.commandType())
                : ResponseCategory.ERROR;
    }

    /** Explains whether an unexpected failure can prove the command's persistence outcome. */
    private String getUnexpectedErrorMessage() {
        return switch (repository.getPersistenceState()) {
            case NOT_ATTEMPTED -> UNEXPECTED_NO_CHANGE_ERROR;
            case COMMITTED -> UNEXPECTED_COMMITTED_ERROR;
            case IN_PROGRESS, UNKNOWN -> UNEXPECTED_UNKNOWN_ERROR;
            default -> UNEXPECTED_UNKNOWN_ERROR;
        };
    }

    /** Displays an error without allowing a failing UI output implementation to escape. */
    private void showMessageSafely(UiOutput output, String message) {
        try {
            output.showMessage(message);
        } catch (RuntimeException e) {
            logUnexpectedFailure("Unexpected UI output failure.", e);
        }
    }

    /** Creates an inert repository for a startup failure without touching user files. */
    private TaskRepository createFallbackRepository(String filePath) {
        try {
            Storage activeStorage = new Storage(filePath);
            Storage archiveStorage = new Storage(Storage.resolveArchivePath(filePath).toString());
            return new TaskRepository(activeStorage, archiveStorage, new TaskList(), new TaskList());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unable to prepare the failed-storage repository.", e);
            return createUnavailableRepository();
        }
    }

    /** Creates a valid in-memory repository used only while commands are disabled. */
    private TaskRepository createUnavailableRepository() {
        Storage activeStorage = new Storage(DEFAULT_DATA_FILE);
        Storage archiveStorage = new Storage(Storage.resolveArchivePath(DEFAULT_DATA_FILE).toString());
        return new TaskRepository(activeStorage, archiveStorage, new TaskList(), new TaskList());
    }

    /** Records technical details while keeping implementation and path details out of the UI. */
    private void logUnexpectedFailure(String message, RuntimeException exception) {
        LOGGER.log(Level.SEVERE, message, exception);
    }

    /**
     * Launches Herta using its default data file.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Herta().run();
    }

    /**
     * Processes one command and returns the response for a graphical user interface.
     *
     * @param input the command entered by the user.
     * @return the response message and exit status generated by the command.
     */
    public HertaResponse getResponse(String input) {
        ResponseCollector responseCollector = new ResponseCollector();
        ResponseCategory responseCategory = processCommand(input, responseCollector);
        boolean isExitRequested = responseCategory == ResponseCategory.EXIT;
        return new HertaResponse(responseCollector.getOutput(), isExitRequested, responseCategory);
    }

    /**
     * Indicates whether startup completed successfully and commands may be processed.
     *
     * @return {@code true} when both task collections are ready.
     */
    public boolean isReady() {
        return loadingError == null;
    }

    /**
     * Returns the startup error, if startup failed.
     *
     * @return the startup error or {@code null} when Herta is ready.
     */
    public String getLoadingError() {
        return loadingError;
    }
}
