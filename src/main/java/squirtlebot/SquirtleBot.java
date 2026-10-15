package squirtlebot;

import squirtlebot.command.Command;
import squirtlebot.exception.SquirtleBotException;
import squirtlebot.exception.StorageException;
import squirtlebot.parser.Parser;
import squirtlebot.storage.Storage;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;

/**
 * Coordinates command parsing, task management, storage, and user interaction
 * for SquirtleBot.
 */
public class SquirtleBot {
    // Message to display if user continues without storage after storage issues
    public static final String CONTINUE_WITHOUT_STORAGE_RESPONSE = "Continuing without storage :)";

    // Prompt to user asking if they would like to continue without storage features
    public static final String STORAGE_ISSUE_PROMPT = "There was an issue with storage :(\n"
            + "Do you want to continue without storage features? [Y/N]";

    private static final int MAX_RESET_COUNT = 2;

    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser;

    /**
     * Constructs a new instance of SquirtleBot.
     */
    public SquirtleBot() {
        storage = new Storage();
        tasks = new TaskList();
        ui = new Ui();
        parser = new Parser();
    }

    /**
     * Constructs a new instance of SquirtleBot using provided values.
     *
     * @param storage instance of storage for writing/reading to storage.
     * @param tasks task list to contain user created tasks.
     * @param parser parses user input into commands.
     * @param ui handles user interactions, displaying of messages, reading input.
     */
    SquirtleBot(Storage storage, TaskList tasks, Parser parser, Ui ui) {
        this.storage = storage;
        this.tasks = tasks;
        this.parser = parser;
        this.ui = ui;
    }

    /**
     * Starts an instance of SquirtleBot.
     * Intended for use with SquirtleBot running in CLI-mode.
     * SquirtleBot will attempt to load previously stored tasks, then start reading user commands.
     * Will continue running till user issues a {@code bye} command or if storage encounters an issue
     * and user opts to not continue.
     */
    public void run() {
        ui.printBanner();

        boolean isLoaded = tryInitializeTasks();
        if (!isLoaded && !shouldContinueWithoutStorage()) {
            return;
        }

        runInteraction();
    }

    private void runInteraction() {
        boolean shouldExit = false;

        while (!shouldExit) {
            String userInput = ui.readInput();
            shouldExit = shouldExitAfterExecutingCommand(userInput);
            ui.printSavedMessage();
        }
    }

    /**
     * Launches SquirtleBot in CLI mode of operation.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        SquirtleBot squirtleBot = new SquirtleBot();
        squirtleBot.run();
    }

    private boolean shouldContinueWithoutStorage() {
        while (true) {
            ui.setSavedMessage("\t" + STORAGE_ISSUE_PROMPT);
            ui.printSavedMessage();

            String userAnswer = ui.readInput();

            if (userAnswer.equals("N")) {
                return false;
            } else if (userAnswer.equals("Y")) {
                this.disableStorage();
                return true;
            }
        }
    }

    /**
     * Returns SquirtleBot's welcome message to be displayed to the user.
     */
    public String getWelcomeMessage() {
        return this.ui.getGuiWelcomeMessage();
    }

    /**
     * Loads previously stored tasks.
     *
     * @return {@code true} if storage was loaded correctly; {@code false} if storage not loaded.
     */
    public boolean tryInitializeTasks() {
        int resetCount = 0;
        while (true) {
            try {
                tasks = storage.loadData();
                return true;
            } catch (StorageException e) {
                if (resetCount >= MAX_RESET_COUNT) {
                    return false;
                }
                storage.resetData();
                resetCount += 1;
            }
        }
    }

    /**
     * Runs the command entered by user, retrieves corresponding output.
     * Intended for use with SquirtleBot running in GUI mode.
     *
     * @param userInput user input containing command to run and relevant parameters.
     * @return output corresponding to user's command.
     */
    public CommandResult getResponse(String userInput) {
        boolean shouldExit = shouldExitAfterExecutingCommand(userInput);
        return new CommandResult(shouldExit, ui.getSavedMessage());
    }

    /**
     * Executes the command indicated by user input.
     *
     * @param userInput string containing command to execute and parameters.
     * @return boolean value indicating whether user would like to exit.
     */
    private boolean shouldExitAfterExecutingCommand(String userInput) {
        boolean shouldExit = false;
        try {
            Command userCommand = parser.parseCommand(userInput);
            shouldExit = userCommand.shouldExit();
            userCommand.execute(tasks, ui, storage);
        } catch (SquirtleBotException e) {
            ui.setSavedMessage(e.getMessage());
        }
        return shouldExit;
    }

    /**
     * Disables storage instance, disallowing use of storage functionality except for loading of data.
     */
    public void disableStorage() {
        storage.disable();
    }
}


