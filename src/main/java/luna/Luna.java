package luna;

import luna.command.Command;
import luna.command.CommandFactory;
import luna.exception.LunaException;
import luna.parser.Parser;
import luna.storage.Storage;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Runs the Luna task manager.
 * Processes commands shared by the CLI and JavaFX GUI.
 *
 * <p>CLI and GUI follow these call paths:
 * <pre>{@code
 * CLI:
 * main()
 *   → run()
 *     → getResponse()
 *       → executeInput()
 *         → Command.execute()
 *
 * GUI:
 * MainWindow.handleUserInput()
 *   → getResponse()
 *     → executeInput()
 *       → Command.execute()
 * }</pre>
 *
 * The CLI and GUI call the public getResponse() method.
 * We hide the actual implementation as a private method executeInput().
 */
public class Luna {
    private static final String WELCOME_MESSAGE = "Hello! I'm Luna!\nWhat can I do for you?";

    private final TaskList tasks;
    private final Storage storage = new Storage();
    private final Ui ui = new Ui();
    private boolean shouldExit;

    /**
     * Creates a Luna instance and loads saved tasks from storage.
     */
    public Luna() {
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (LunaException e) {
            ui.showError("Warning: could not load saved tasks.");
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
        shouldExit = false;
    }

    /**
     * Starts the application.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Luna().run();
    }

    /**
     * Runs the main program loop until Luna receives the exit command.
     */
    public void run() {
        ui.showWelcome(getWelcomeMessage());

        while (!shouldExit) {
            String input = ui.readCommand();
            String response = getResponse(input);
            ui.showResponse(response);

            if (!shouldExit) {
                ui.showLine();
            }
        }

        ui.close();
    }

    /**
     * Returns Luna's welcome message.
     *
     * @return Welcome message shared by the CLI and GUI.
     */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Processes one command and returns Luna's response.
     *
     * @param input User command to process.
     * @return Response to display.
     */
    public String getResponse(String input) {
        try {
            return executeInput(input);
        } catch (LunaException e) {
            return e.getMessage();
        }
    }

    /**
     * Parses and executes one user command.
     *
     * @param input User command to execute.
     * @return Response produced by the command.
     * @throws LunaException If the command is invalid or cannot be executed.
     */
    private String executeInput(String input) throws LunaException {
        String[] parts = Parser.parse(input);
        assert parts.length == 2 : "Parser.parse() should return [command, rest of arguments]";
        String commandWord = parts[0];
        String commandArgs = parts[1];

        Command command = CommandFactory.create(commandWord, commandArgs, tasks, storage);
        String response = command.execute();

        if (command.isExit()) {
            shouldExit = true;
        }

        return response;
    }

    /**
     * Returns whether Luna has received the exit command (for GUI).
     *
     * @return True if Luna should exit.
     */
    public boolean isExit() {
        return shouldExit;
    }
}
