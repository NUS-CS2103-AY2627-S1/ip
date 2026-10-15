package bob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import bob.command.AddCommand;
import bob.command.Command;
import bob.command.DeleteCommand;
import bob.command.MarkCommand;
import bob.command.UnmarkCommand;
import bob.task.TaskList;

/**
 * Runs the Bob task-management chatbot.
 */
public class Bob {
    private final Storage storage;
    private final Ui ui;
    private boolean isInitialized;
    private boolean isStorageReadable = true;
    private TaskList tasks;

    /**
     * Creates a Bob application that saves tasks at the given file path.
     *
     * @param filePath location of the task data file
     */
    public Bob(String filePath) {
        this.storage = new Storage(Path.of(filePath));
        this.ui = new Ui();
        this.isInitialized = false;
        this.tasks = new TaskList();
    }

    /**
     * Starts Bob's command-reading loop and runs until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();

        initialize(ui);
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            String input = ui.readCommand();

            try {
                isExit = executeCommand(input, ui);
            } finally {
                ui.showDivider();
            }
        }
        ui.close();
    }

    /**
     * Processes one GUI command using Bob's existing parser, commands, task list, and storage.
     *
     * @param input command entered in the GUI
     * @return Bob's response for display in a dialog box
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try (PrintStream responseStream = new PrintStream(responseBytes, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(responseStream);
            initialize(responseUi);
            executeCommand(input, responseUi);
        }
        return responseBytes.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Loads saved tasks once for either the console or GUI interface.
     *
     * @param activeUi interface that should receive any loading error
     */
    private void initialize(Ui activeUi) {
        if (isInitialized) {
            return;
        }
        tasks = loadTasks(activeUi);
        isInitialized = true;
    }

    /**
     * Parses and executes one command, reporting expected errors through the active UI.
     *
     * @param input command to execute
     * @param activeUi interface that receives the command response
     * @return whether the command requests an exit
     */
    private boolean executeCommand(String input, Ui activeUi) {
        // Commands must run against loaded state so startup cannot overwrite their changes.
        assert isInitialized : "Tasks must be initialized before executing a command";
        try {
            Command command = Parser.parse(input);
            if (!isStorageReadable && (command instanceof AddCommand || command instanceof DeleteCommand
                    || command instanceof MarkCommand || command instanceof UnmarkCommand)) {
                throw new BobException("saved tasks could not be loaded; fix the data file before changing tasks");
            }
            command.execute(tasks, activeUi, storage);
            return command.isExit();
        } catch (BobException exception) {
            activeUi.showError(exception.getMessage());
        } catch (IOException exception) {
            activeUi.showError("couldn't save your tasks; nothing was changed");
        }
        return false;
    }

    /**
     * Loads saved tasks, falling back to an empty list if the data cannot be used.
     */
    private TaskList loadTasks(Ui activeUi) {
        try {
            return new TaskList(storage.load());
        } catch (StorageException exception) {
            isStorageReadable = false;
            activeUi.showError("couldn't load saved tasks: " + exception.getMessage());
            if (activeUi == ui) {
                activeUi.showDivider();
            }
            return new TaskList();
        }
    }

    /**
     * Starts Bob using the default task data file.
     *
     * @param args command-line arguments, which Bob does not use
     */
    public static void main(String[] args) {
        new Bob("data/bob.txt").run();
    }
}
