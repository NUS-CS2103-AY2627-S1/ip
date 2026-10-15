package chudgpt;

import chudgpt.command.AddTaskCommand;
import chudgpt.command.Command;
import chudgpt.exception.ChudException;
import chudgpt.parser.Parser;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/**
 * The entry point for the ChudGPT chatbot application.
 */
public class ChudGpt {
    private final TaskList tasks;
    private final Parser parser;
    private final Ui ui;
    /** Storage used to load and save task data. */
    private final Storage storage;
    private final String startupWarning;
    private boolean hasExited;

    /**
     * Creates a ChudGPT application backed by the specified data file.
     *
     * @param filePath path to the file used to load and save tasks.
     */
    public ChudGpt(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();

        TaskList loadedTasks;
        String loadWarning = "";
        try {
            Storage.LoadResult loadResult = storage.loadWithWarnings();
            loadedTasks = new TaskList(loadResult.tasks());
            if (!loadResult.warnings().isEmpty()) {
                loadWarning = ui.getLoadWarningMessage(loadResult.warnings());
            }
        } catch (ChudException e) {
            loadWarning = ui.getLoadErrorMessage() + "\nDetails:\n  " + e.getMessage();
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
        startupWarning = loadWarning;
    }

    /** Runs the command-line application. */
    public void run() {
        ui.display(ui.getWelcomeMessage());
        if (!startupWarning.isEmpty()) {
            ui.display(startupWarning);
        }

        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            ui.display(ui.getDivider());
            CommandResult result = executeCommand(input);
            updateExitStatus(result);
            ui.display(result.response());
            ui.display(ui.getDivider());
            isExit = hasExited;
        }
    }

    /**
     * Returns ChudGPT's response to one user command.
     *
     * @param input the command entered by the user.
     * @return ChudGPT's response.
     */
    public String getResponse(String input) {
        return getResponseDetails(input).text();
    }

    /**
     * Returns ChudGPT's response and its presentation category.
     *
     * @param input the command entered by the user.
     * @return response details suitable for presentation by the GUI.
     */
    public Response getResponseDetails(String input) {
        CommandResult result = executeCommand(input);
        updateExitStatus(result);
        return new Response(result.response(), result.responseType());
    }

    /**
     * Returns whether ChudGPT has received an exit command.
     *
     * @return {@code true} if ChudGPT has received an exit command.
     */
    public boolean hasExited() {
        return hasExited;
    }

    /**
     * Returns ChudGPT's welcome message.
     *
     * @return ChudGPT's welcome message.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Returns a compact welcome message suitable for the GUI.
     *
     * @return welcome message without the console logo and dividers.
     */
    public String getGuiWelcomeMessage() {
        String welcomeMessage = "Hello! I'm ChudGPT.\nWhat can I do for you?";
        return startupWarning.isEmpty()
                ? welcomeMessage
                : welcomeMessage + "\n\n" + startupWarning;
    }

    /** Executes one command and returns its response and exit status. */
    private CommandResult executeCommand(String input) {
        try {
            Command command = parser.parse(input);
            assert command != null : "Parser should return a command for valid input";

            String response = command.execute(tasks, ui, storage);
            assert response != null : "Commands should return a response";
            ResponseType responseType = command instanceof AddTaskCommand
                    ? ResponseType.TASK_ADDED
                    : ResponseType.NORMAL;
            return new CommandResult(response, command.isExit(), responseType);
        } catch (ChudException e) {
            return new CommandResult(ui.getErrorMessage(e.getMessage()), false, ResponseType.ERROR);
        }
    }

    /** Updates the application exit status from a command result. */
    private void updateExitStatus(CommandResult result) {
        assert result != null : "Command result should not be null";
        hasExited = hasExited || result.isExit();
    }

    /** Identifies how an application response should be presented by the GUI. */
    public enum ResponseType {
        NORMAL,
        ERROR,
        TASK_ADDED
    }

    /** Contains text and presentation information for one application response. */
    public record Response(String text, ResponseType responseType) {
    }

    /** Stores the internal outcome of executing one command. */
    private record CommandResult(String response, boolean isExit, ResponseType responseType) {
    }

    /**
     * Starts the ChudGPT command-line application.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new ChudGpt("data/tasks.txt").run();
    }
}
