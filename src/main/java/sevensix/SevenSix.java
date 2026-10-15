package sevensix;

import java.nio.file.Path;

/**
 * Connects parsed commands to the session executor and provides the console entry point.
 *
 * <p>Command behavior belongs to {@link Command} implementations, parsing to {@link Parser},
 * response wording to {@link Ui}, and persistence and recovery to {@link CommandExecutor}.
 */
public class SevenSix {
    /** Default relative path for persisted tasks. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "sevensix.txt");
    /** System property that overrides the default data-file path during automated runs. */
    private static final String DATA_FILE_PROPERTY = "sevensix.data.file";

    /** Owns the task session and coordinates persistence and rollback. */
    private final CommandExecutor executor;

    /**
     * Creates a chatbot using the configured data-file path.
     */
    public SevenSix() {
        this(new TaskStorage(resolveDataFile()));
    }

    /**
     * Creates a chatbot using the supplied data-file path.
     *
     * @param dataFile the path used to persist tasks.
     */
    public SevenSix(Path dataFile) {
        this(new TaskStorage(dataFile));
    }

    /** Initializes a chatbot using storage whose configuration has already been checked.
     *
     * @param storage the task storage to load.
     */
    private SevenSix(TaskStorage storage) {
        executor = new CommandExecutor(storage);
    }

    /**
     * Runs the original console interface.
     *
     * @param args command-line arguments, which are not used by this application.
     */
    public static void main(String[] args) {
        SevenSix chatbot = new SevenSix();
        ConsoleUi.printGreeting(chatbot.getStartupWarning());
        ConsoleUi.runConsoleLoop(chatbot);
    }

    /**
     * Resolves the data-file path from the optional system property.
     *
     * @return the configured data-file path.
     */
    private static String resolveDataFile() {
        String configuredPath = System.getProperty(DATA_FILE_PROPERTY);
        return configuredPath == null || configuredPath.isBlank()
                ? DEFAULT_DATA_FILE.toString()
                : configuredPath;
    }

    /**
     * Processes one command and returns the response that should be shown to the user.
     *
     * @param command the command entered by the user.
     * @return the chatbot response, without console separators.
     */
    public String getResponse(String command) {
        try {
            return executor.execute(Parser.parse(command));
        } catch (SevenSixException exception) {
            return Ui.formatError(exception.getMessage());
        }
    }

    /** Returns a startup warning for both console and graphical interfaces.
     *
     * @return the storage warning, or an empty string when loading succeeded.
     */
    public String getStartupWarning() {
        return executor.getStartupWarning();
    }

}
