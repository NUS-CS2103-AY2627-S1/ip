package brucli.ui;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles all terminal input and output for BruCLI.
 */
public class Ui {
    private final Scanner scanner;
    private final PrintStream output;
    private final boolean isTerminalOutput;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out;
        isTerminalOutput = true;
    }

    /**
     * Creates a UI that sends messages to the supplied output stream.
     *
     * @param output Destination for messages produced by commands.
     */
    public Ui(PrintStream output) {
        scanner = new Scanner("");
        this.output = output;
        isTerminalOutput = false;
    }

    /**
     * Displays the opening banner and welcome response.
     */
    public void showWelcome(String banner) {
        output.println(banner);
        showMessage(Messages.welcome());
    }

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the goodbye response.
     */
    public void showGoodbye() {
        showMessage(Messages.goodbye());
    }

    /**
     * Confirms that a todo was added.
     */
    public void showTodoAdded() {
        showMessage(Messages.todoAdded());
    }

    /**
     * Confirms that a deadline was added.
     */
    public void showDeadlineAdded() {
        showMessage(Messages.deadlineAdded());
    }

    /**
     * Confirms that an event was added.
     */
    public void showEventAdded() {
        showMessage(Messages.eventAdded());
    }

    /**
     * Introduces a task-list result.
     */
    public void showListHeader() {
        showMessage(Messages.listHeader());
    }

    /**
     * Displays formatted task-list content.
     */
    public void showTaskList(String taskList) {
        showMessage(taskList);
    }

    /**
     * Displays the tasks found for a keyword search.
     *
     * @param taskList Formatted matching tasks, or a no-matches message.
     */
    public void showFindResults(String taskList) {
        showMessage(Messages.findResults(taskList));
    }

    /**
     * Displays the supported commands and usage examples.
     */
    public void showHelp() {
        showMessage(Messages.help());
    }

    /**
     * Displays a playful response to the game command.
     */
    public void showGameResponse() {
        showMessage(Messages.gameResponse());
    }

    /** Confirms that a task was marked. */
    public void showTaskMarked() {
        showMessage(Messages.taskMarked());
    }

    /**
     * Confirms that a task was unmarked.
     */
    public void showTaskUnmarked() {
        showMessage(Messages.taskUnmarked());
    }

    /**
     * Confirms that a task was deleted.
     */
    public void showTaskDeleted() {
        showMessage(Messages.taskDeleted());
    }

    /**
     * Reports an unrecognized command.
     */
    public void showUnknownCommand() {
        showMessage(Messages.unknownCommand());
    }

    /**
     * Reports that elevated privileges are unavailable.
     */
    public void showSudoDenied() {
        showMessage(Messages.sudoDenied());
    }

    /**
     * Reports that tasks could not be loaded.
     */
    public void showLoadingError() {
        showMessage(Messages.loadingError());
    }

    /**
     * Reports that tasks could not be saved.
     */
    public void showSavingError() {
        showMessage(Messages.savingError());
    }

    /**
     * Displays a validation or command error.
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Displays a message using BruCLI's standard prompt layout.
     */
    private void showMessage(String message) {
        if (isTerminalOutput) {
            output.println(Messages.formatResponse(message));
        } else {
            output.println(message);
        }
    }
}
