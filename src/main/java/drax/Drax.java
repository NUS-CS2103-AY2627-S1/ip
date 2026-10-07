package drax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds Drax's application state and exposes it to console and graphical interfaces.
 */
public class Drax {
    private static final String GREETING = """
            Infinite Salutations! I'm Drax!
            What's on your mind today?""";

    private final CommandExecutor executor;
    private final List<String> startupWarnings;

    /** Loads saved tasks and prepares Drax to process commands. */
    public Drax() {
        Storage storage = new Storage();
        Storage.LoadResult loadResult;
        try {
            loadResult = storage.load();
        } catch (IOException e) {
            ArrayList<String> warnings = new ArrayList<>();
            warnings.add("Sorry! I couldn't read your saved tasks! Starting with an empty list.");
            loadResult = new Storage.LoadResult(new ArrayList<>(), warnings);
        }

        TaskList tasks = new TaskList(loadResult.tasks());
        this.executor = new CommandExecutor(tasks, storage);
        this.startupWarnings = List.copyOf(loadResult.warnings());
    }

    /**
     * Starts the terminal interface and processes commands until the user exits.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Drax drax = new Drax();
        Ui ui = new Ui();
        ui.show(ui.getConsoleGreeting(drax.greet()));

        while (ui.hasNextCommand()) {
            CommandExecutor.ExecutionResult result = drax.executeCommand(ui.readCommand());
            ui.show(result.response());
            if (result.outcome() == CommandExecutor.Outcome.EXIT) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Returns the greeting and any warnings produced while loading saved tasks.
     *
     * @return the startup message to display
     */
    public String greet() {
        if (startupWarnings.isEmpty()) {
            return GREETING;
        }
        String greetingWithWarnings = GREETING + "\n" + String.join("\n", startupWarnings);
        return greetingWithWarnings;
    }

    /**
     * Processes one command and returns the response text for a graphical interface.
     *
     * @param input raw command entered by the user.
     * @return text produced by executing the command
     */
    public String getResponse(String input) {
        return executeCommand(input).response();
    }

    /**
     * Parses string input and categorizes commands into groups for differentiated CSS styling.
     *
     * @param input raw command entered by the user.
     * @return command type category for CSS styling
     */
    public String getCommandType(String input) {
        switch (Parser.parse(input).type()) {
            case MARK, UNMARK -> {
                return "ChangeMarkCommand";
            }
            case TODO, DEADLINE, EVENT -> {
                return "AddCommand";
            }
            case FIND, LIST -> {
                return "ListCommand";
            }
            case DELETE, UNKNOWN -> {
                return "DeleteCommand";
            }
            default -> {
                return "";
            }
        }
    }

    /**
     * Processes one command while retaining its control-flow outcome for the console loop.
     *
     * @param input raw command entered by the user.
     * @return response text and whether command processing should continue
     */
    public CommandExecutor.ExecutionResult executeCommand(String input) {
        return executor.execute(Parser.parse(input));
    }
}
