package et;

import et.command.Command;
import et.exception.ETException;
import et.parser.Parser;
import et.storage.Storage;
import et.task.TaskList;
import et.ui.Ui;

import java.io.IOException;

/**
 * Entry point for the ET task companion.
 */
public class ET {
    /** The component that handles console input and output. */
    private final Ui ui;

    /** The component that loads and saves the task list. */
    private final Storage storage;

    /** The component that converts user input into commands. */
    private final Parser parser;

    /** The tasks managed during the current ET session. */
    private final TaskList tasks;

    /** Whether saved tasks could not be loaded when this session started. */
    private final boolean hasLoadingError;

    /** Creates ET's collaborating components. */
    public ET() {
        this(new Ui(), new Storage(), new Parser());
    }

    /** Creates ET with replaceable collaborators for automated tests. */
    ET(Ui ui, Storage storage, Parser parser) {
        assert ui != null : "UI must be provided";
        assert storage != null : "Storage must be provided";
        assert parser != null : "Parser must be provided";

        this.ui = ui;
        this.storage = storage;
        this.parser = parser;

        TaskList loadedTasks;
        boolean didLoadingFail = false;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (IOException e) {
            loadedTasks = new TaskList();
            didLoadingFail = true;
        }
        tasks = loadedTasks;
        hasLoadingError = didLoadingFail;
    }

    /**
     * Starts ET and processes commands from standard input until an exit command is run.
     */
    public void run() {
        ui.showWelcome();
        if (hasLoadingError) {
            ui.showLoadingError();
        }
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showDivider();
                Command command = parser.parseCommand(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ETException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showDivider();
            }
        }
    }

    /**
     * Returns ET's reply to one command for use by the graphical interface.
     *
     * @param input the complete command entered by the user
     * @return ET's response to the command
     */
    public String getResponse(String input) {
        return getCommandResult(input).response();
    }

    /**
     * Executes one graphical-interface command and reports its reply and exit state.
     *
     * @param input the complete command entered by the user
     * @return the result containing ET's response and whether the application should close
     */
    public CommandResult getCommandResult(String input) {
        StringBuilder response = new StringBuilder();
        Ui responseUi = new Ui(message -> {
            if (!response.isEmpty()) {
                response.append(System.lineSeparator());
            }
            response.append(message.strip());
        });

        boolean shouldExit = false;
        try {
            Command command = parser.parseCommand(input.trim());
            command.execute(tasks, responseUi, storage);
            shouldExit = command.isExit();
        } catch (ETException e) {
            responseUi.showError(e.getMessage());
        }
        return new CommandResult(response.toString(), shouldExit);
    }

    /**
     * Returns the greeting displayed when the graphical interface opens.
     *
     * @return ET's greeting, including a loading warning when needed
     */
    public String getWelcomeMessage() {
        String welcomeMessage = ui.getWelcomeMessage();
        if (hasLoadingError) {
            return welcomeMessage + System.lineSeparator()
                    + "Oh... my memory box would not open, so I'm starting with an empty list.";
        }
        return welcomeMessage;
    }

    /**
     * Starts ET's graphical application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Launcher.main(args);
    }

    /**
     * Describes the outcome of one command submitted through the graphical interface.
     *
     * @param response the message ET produced
     * @param shouldExit whether the command should close the application
     */
    public record CommandResult(String response, boolean shouldExit) {
    }
}
