package monkey;

import monkey.command.CommandAction;
import monkey.exception.MonkeyException;
import monkey.model.TaskList;
import monkey.parser.Parser;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** A simple command-line task manager. */
public class Monkey {
    private static final String DEFAULT_FILE_PATH = "data/monkey.txt";

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private String startupWarning;

    /** Creates a Monkey chatbot that stores tasks at the default file path. */
    public Monkey() {
        this(DEFAULT_FILE_PATH);
    }

    /** Creates a Monkey chatbot that stores tasks at the supplied file path. */
    public Monkey(String filePath) {
        parser = new Parser();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
        startupWarning = storage.getLastError();
    }

    /** Runs the task manager and processes commands until the user says bye. */
    public static void main(String[] args) {
        new Monkey().run();
    }

    /** Returns Monkey's response to one command for use by graphical clients. */
    public String getResponse(String input) {
        return processCommand(input).response();
    }

    /** Processes one command and returns its response and exit state. */
    public CommandResult processCommand(String input) {
        StringBuilder response = new StringBuilder();
        Ui responseUi = new Ui(message -> {
            if (!response.isEmpty()) {
                response.append('\n');
            }
            response.append(message);
        });
        showStartupWarning(responseUi);

        boolean shouldExit = false;
        try {
            shouldExit = executeCommand(input, responseUi).isExit();
        } catch (MonkeyException | IllegalArgumentException e) {
            responseUi.showMessage("OOPS! Monkey says: " + e.getMessage());
        }
        return new CommandResult(response.toString(), shouldExit);
    }

    /** Runs the command-line interface until the user exits or input ends. */
    private void run() {
        Ui ui = new Ui();
        ui.showWelcome();
        showStartupWarning(ui);

        String command;
        while ((command = ui.readCommand()) != null) {
            ui.showSeparator();

            boolean shouldExit = false;
            try {
                shouldExit = executeCommand(command, ui).isExit();
            } catch (MonkeyException | IllegalArgumentException e) {
                ui.showMessage("OOPS! Monkey says: " + e.getMessage());
            }

            ui.showSeparator();
            if (shouldExit) {
                break;
            }
        }
    }

    /** Executes one command and returns the action that was performed. */
    private CommandAction executeCommand(String input, Ui targetUi) throws MonkeyException {
        if (input == null || input.trim().isEmpty()) {
            throw new MonkeyException("This monkey heard nothing! Please swing over a command.");
        }

        CommandAction command = parser.parseAction(input);
        command.execute(tasks, targetUi, storage);
        return command;
    }

    private void showStartupWarning(Ui ui) {
        if (startupWarning == null) {
            return;
        }
        ui.showMessage("OOPS! Monkey says: " + startupWarning);
        startupWarning = null;
    }
}
