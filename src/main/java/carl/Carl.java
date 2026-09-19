package carl;

import java.util.Objects;

import carl.commands.Command;
import carl.commands.CommandResult;
import carl.exceptions.CarlException;
import carl.parser.CarlParser;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.MainApplication;
import carl.ui.Ui;
import javafx.application.Application;

/**
 * Represents the main entry point and controller of the Carl chatbot application.
 */
public class Carl {
    /** Name displayed in the window title and application header. */
    public static final String BOT_NAME = "Carl · Night-Shift Dispatcher";
    private final TaskManager taskManager;
    private TaskList tasks;
    private Ui ui;
    private CarlParser parser;
    private String startupWarning;

    /** Creates Carl with the default local task storage. */
    public Carl() {
        this(new TaskManager());
    }

    /** Creates Carl with injected storage so core behavior can be tested in isolation. */
    Carl(TaskManager taskManager) {
        this.taskManager = Objects.requireNonNull(taskManager);
    }

    /**
     * Launches the Carl application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }

    /**
     * Generates the welcome message for the user interface.
     *
     * @return Carl's welcome message.
     */
    public String getWelcomeMessage() {
        return ui.showWelcome();
    }

    /**
     * Processes user input and returns the result of the executed command.
     *
     * @param input The raw input string entered by the user.
     * @return The result of executing the parsed command, or an error result if parsing/execution fails.
     */
    public CommandResult getResponse(String input) {
        try {
            Command command = parser.parseCommands(input);

            assert ui != null : "Ui instance should never be null";
            assert taskManager != null : "Task Manager instance should never be null";
            assert tasks != null : "Task List instance should never be null";

            return command.onRun(ui, taskManager, tasks, input);

        } catch (CarlException e) {
            return CommandResult.createError(e.getMessage());
        } catch (RuntimeException e) {
            return CommandResult.createError(
                    "An unexpected error occurred. The command could not be completed safely.");
        }
    }

    /**
     * Initializes the core dependencies and services required for the chatbot to run.
     */
    public void start() {
        ui = new Ui();
        tasks = new TaskList(taskManager.createSave());
        startupWarning = taskManager.getStartupWarning();
        parser = new CarlParser();
    }

    /**
     * Returns a recoverable warning encountered while loading task storage.
     *
     * @return warning text, or {@code null} when startup completed normally.
     */
    public String getStartupWarning() {
        return startupWarning;
    }
}
