package beemo;

import java.nio.file.Path;

import beemo.command.Command;
import beemo.parser.Parser;
import beemo.storage.Storage;
import beemo.task.TaskList;
import beemo.ui.Ui;

/**
 * Coordinates Beemo's user interface, task list, command parsing, and storage.
 */
public class Beemo {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private String loadingError;

    /**
     * Creates Beemo using the specified task data file.
     *
     * @param filePath Path of the task data file.
     */
    public Beemo(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (BeemoException e) {
            tasks = new TaskList();
            loadingError = e.getMessage();
        }
        assert tasks != null : "Task list must be initialized";
    }

    /**
     * Executes one command and returns the response intended for the user.
     *
     * @param input Command entered by the user.
     * @return Beemo's response to the command.
     */
    public String getResponse(String input) {
        return getCommandResult(input).response();
    }

    /**
     * Executes one command and returns its response and exit status.
     *
     * @param input Command entered by the user.
     * @return Result containing the response and whether Beemo should exit.
     */
    public CommandResult getCommandResult(String input) {
        StringBuilder response = new StringBuilder();
        Ui responseUi = new Ui(line -> {
            if (!response.isEmpty()) {
                response.append(System.lineSeparator());
            }
            response.append(line);
        });

        boolean isExit = executeCommand(input, responseUi);
        return new CommandResult(response.toString(), isExit);
    }

    /**
     * Runs Beemo until the user enters the exit command or input ends.
     */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError);
            ui.showLine();
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                isExit = executeCommand(fullCommand, ui);
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Executes a command using the supplied UI and reports whether Beemo should exit.
     *
     * @param input Command entered by the user.
     * @param commandUi UI that receives command output.
     * @return True if the command exits Beemo, otherwise false.
     */
    private boolean executeCommand(String input, Ui commandUi) {
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, commandUi, storage);
            return command.isExit();
        } catch (BeemoException e) {
            commandUi.showError(e.getMessage());
            return false;
        }
    }

    /**
     * Starts Beemo using the default task data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Beemo(Path.of("data", "beemo.txt")).run();
    }
}
