package answerme;

import answerme.command.Command;
import answerme.command.CommandResult;
import answerme.exception.AnswerMeException;
import answerme.parser.Parser;
import answerme.storage.Storage;
import answerme.storage.TaskLoadResult;
import answerme.task.TaskList;
import answerme.ui.ResponseType;
import answerme.ui.Ui;

/**
 * Represents the AnswerMe task-management chatbot.
 */
public class AnswerMe {
    private Storage storage;
    private TaskList taskList;
    private Ui ui;

    /**
     * Constructs the chatbot application and loads saved tasks
     * from storage if available.
     *
     * @param isCliInstance Whether the application is running
     *                      in command-line mode.
     */
    public AnswerMe(boolean isCliInstance) {
        storage = new Storage();
        ui = new Ui(isCliInstance);
        try {
            TaskLoadResult loadResult = storage.loadTasks();
            taskList = new TaskList(loadResult.taskList());
            if (loadResult.hasWarnings()) {
                ui.showLoadingWarning(loadResult.warnings());
            }
        } catch (AnswerMeException exception) {
            ui.showLoadingError(exception.getMessage());
            storage.disableSaving();
            taskList = new TaskList();
        }
    }

    /**
     * Runs the application until the user enters the exit command.
     */
    public void run() {
        if (ui.getLoadingErrorMessage() != null) {
            return;
        }
        ui.showWelcome();
        boolean shouldExit = false;
        while (!shouldExit) {
            String input = ui.readUserInput();
            CommandResult result = processCommand(input);
            shouldExit = result.shouldExit();
        }
    }

    /**
     * Processes the user's input by parsing and executing commands.
     *
     * @param input The user's input.
     * @return The command result containing the response and exit status.
     */
    public CommandResult processCommand(String input) {
        try {
            Command command = Parser.parse(input);
            command.execute(taskList, ui, storage);
            return new CommandResult(ui.getLatestResponse(), ui.getLatestResponseType(), command.isExit());
        } catch (AnswerMeException exception) {
            ui.showError(exception.getMessage());
            return new CommandResult(ui.getLatestResponse(), ResponseType.ERROR, false);
        }
    }

    /**
     * Returns the chatbot's welcome message.
     *
     * @return The chatbot's welcome message.
     */
    public String showWelcomeMessage() {
        return ui.getShortWelcome();
    }

    /**
     * Returns the loading error message when saved tasks could not be loaded.
     *
     * @return The loading error message, or {@code null} if loading succeeded.
     */
    public String getLoadingErrorMessage() {
        return ui.getLoadingErrorMessage();
    }

    /**
     * Returns the loading warning message when one or more saved tasks were skipped.
     *
     * @return The loading warning message, or {@code null} if no warnings occurred.
     */
    public String getLoadingWarningMessage() {
        return ui.getLoadingWarningMessage();
    }

    /**
     * Starts a new instance of the AnswerMe chatbot application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        AnswerMe answerMeBot = new AnswerMe(true);
        answerMeBot.run();
    }
}
