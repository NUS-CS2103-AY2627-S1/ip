package yourhand;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import yourhand.commands.Command;
import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Executes YourHand commands independently of a particular user interface. */
public class YourHandEngine {
    private static final Logger LOGGER = Logger.getLogger(YourHandEngine.class.getName());
    private final Storage storage;
    private final TaskList taskList;
    private final String startupWarning;

    /** Creates an engine using the default YourHand data file. */
    public YourHandEngine() {
        storage = new Storage();
        ByteArrayOutputStream startupOutput = new ByteArrayOutputStream();
        taskList = YourHand.loadTasks(storage, new Ui(new PrintStream(startupOutput)));
        startupWarning = startupOutput.toString().trim();
    }

    /** Creates an engine with collaborators supplied by the caller. */
    public YourHandEngine(Storage storage, TaskList taskList) {
        this.storage = Objects.requireNonNull(storage, "engine storage must be provided");
        this.taskList = Objects.requireNonNull(taskList, "engine task list must be provided");
        startupWarning = "";
    }

    /** Returns whether loading saved data produced a startup warning. */
    public boolean hasStartupWarning() {
        return !startupWarning.isEmpty();
    }

    /** Returns the startup warning that should be shown to the user, if any. */
    public String getStartupWarning() {
        return startupWarning;
    }

    /** Executes one command and returns the text that should be shown to the user. */
    public String execute(String command) {
        return executeWithResult(command).message();
    }

    /** Executes one command and reports whether the command completed successfully. */
    public ExecutionResult executeWithResult(String command) {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(response));
        try {
            Command parsedCommand = YourHand.parseCommand(command);
            parsedCommand.execute(taskList, ui, storage);
            return new ExecutionResult(response.toString(), true);
        } catch (YourHandException exception) {
            ui.showError(exception.getMessage());
            return new ExecutionResult(response.toString(), false);
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING, "Unexpected error while executing a command", exception);
            ui.showError("I couldn't complete that command. Please try again or type help.");
            return new ExecutionResult(response.toString(), false);
        }
    }

    /** Returns the current task list for graphical views. */
    TaskList getTaskList() {
        return taskList;
    }
}
