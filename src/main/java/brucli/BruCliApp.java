package brucli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import brucli.command.Command;
import brucli.parser.Parser;
import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.CommandResponse;
import brucli.ui.Messages;
import brucli.ui.ResponseType;
import brucli.ui.Ui;

/**
 * Coordinates BruCLI's parser, task list, storage, and user interface.
 */
public class BruCliApp {

    private final TaskList tasks;
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final boolean loadingFailed;

    /**
     * Creates a BruCLI application backed by the default task data file.
     */
    public BruCliApp() {
        this("tasks.txt");
    }

    /**
     * Creates a BruCLI application backed by the given task file.
     */
    public BruCliApp(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();

        TaskList loadedTasks;
        boolean loadHadError = false;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (IOException e) {
            loadedTasks = new TaskList();
            loadHadError = true;
        }

        tasks = loadedTasks;
        loadingFailed = loadHadError;
    }

    /**
     * Starts BruCLI's command-processing loop.
     */
    public void run() {
        ui.showWelcome(Messages.banner());

        if (loadingFailed) {
            ui.showLoadingError();
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();

            if (executeCommand(input, ui).shouldExit()) {
                return;
            }
        }
    }

    /**
     * Executes a user command and returns the response for a graphical interface.
     *
     * @param input User command to execute.
     * @return Text and visual category produced while executing the command.
     */
    public CommandResponse getResponse(String input) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        ExecutionResult executionResult;
        try (PrintStream responseOutput = new PrintStream(
                responseBytes,
                true,
                StandardCharsets.UTF_8
        )) {
            executionResult = executeCommand(input, new Ui(responseOutput));
        }
        String responseText = responseBytes.toString(StandardCharsets.UTF_8).stripTrailing();
        return new CommandResponse(responseText, executionResult.responseType());
    }

    /**
     * Executes a command and reports expected errors through the supplied UI.
     */
    private ExecutionResult executeCommand(String input, Ui targetUi) {
        try {
            Command command = parser.parse(input);
            command.execute(tasks, targetUi, storage);
            return new ExecutionResult(command.isExit(), command.getResponseType());
        } catch (IllegalArgumentException e) {
            targetUi.showError(e.getMessage());
        } catch (IOException e) {
            targetUi.showSavingError();
        }
        return new ExecutionResult(false, ResponseType.ERROR);
    }

    /**
     * Starts BruCLI using the default task data file.
     */
    public static void main(String[] args) {
        new BruCliApp("tasks.txt").run();
    }

    private record ExecutionResult(boolean shouldExit, ResponseType responseType) {
    }
}
