package remy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import remy.command.Command;
import remy.exception.RemyException;
import remy.parser.Parser;
import remy.storage.Storage;
import remy.storage.StorageException;
import remy.storage.StorageLoadResult;
import remy.task.TaskList;
import remy.ui.Ui;

/**
 * Coordinates Remy's command parsing, task management, persistence, and responses.
 */
public class Remy {
    /** Divider used by console messages but omitted from GUI chat bubbles. */
    private static final String MESSAGE_DIVIDER = "____________________________________________________________";

    /** Default file used to persist tasks between sessions. */
    private static final String DEFAULT_TASK_FILE = "./data/remy.txt";

    /** Stores the tasks managed during this chat session. */
    private TaskList tasks;

    /** Interprets user commands and their arguments. */
    private final Parser parser;

    /** Loads and saves tasks between chat sessions. */
    private final Storage storage;

    /** Warning produced when some or all saved tasks could not be loaded at startup. */
    private String loadingWarning;

    /** Records whether the user has ended this chat session. */
    private boolean hasExited;

    /** Creates a Remy instance that stores tasks in the default data file. */
    public Remy() {
        this(DEFAULT_TASK_FILE);
    }

    /**
     * Creates a Remy instance that stores tasks in the given file.
     *
     * @param taskFile file used to persist tasks
     */
    public Remy(String taskFile) {
        parser = new Parser();
        storage = new Storage(taskFile);
        loadTasks();
    }

    /** Runs the chatbot command loop. */
    public static void main(String[] args) {
        new Remy().runCommandLoop();
    }

    /** Runs Remy through the command-line user interface. */
    private void runCommandLoop() {
        Ui ui = new Ui();
        ui.showGreeting();
        if (loadingWarning != null) {
            ui.showLoadingWarning(loadingWarning);
        }

        while (!hasExited) {
            try {
                String message = ui.readCommand();
                if (message == null) {
                    throw new RemyException("You didn't type anything bruh D:");
                }

                executeCommand(message, ui);
            } catch (RemyException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.close();
        ui.showFarewell();
    }

    /** Loads saved tasks from the hard disk when the chatbot starts. */
    private void loadTasks() {
        try {
            StorageLoadResult loadResult = storage.loadWithRecoveryDetails();
            tasks = loadResult.getTasks();
            if (loadResult.hasInvalidLines()) {
                String lineNumbers = loadResult.getInvalidLineNumbers().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                loadingWarning = "Skipped invalid saved task data on line(s) " + lineNumbers
                        + " of \"" + storage.getTaskFilePath() + "\". Valid tasks were loaded; invalid lines "
                        + "will be discarded when tasks are next saved.";
            }
        } catch (StorageException e) {
            tasks = new TaskList();
            loadingWarning = e.getMessage() + " Remy started with an empty task list.";
        }

        assert tasks != null : "Task list must be initialized after loading";
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input chat message to process
     * @return Remy's response to the message
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(responseBuffer, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(responseOutput);
            try {
                if (input == null || input.isBlank()) {
                    throw new RemyException("Please type a command so I can help you.");
                }
                if (hasExited) {
                    throw new RemyException("This chat has ended. Restart Remy to begin a new session.");
                }

                executeCommand(input, responseUi);
                if (hasExited) {
                    responseUi.showFarewell();
                }
            } catch (RemyException e) {
                responseUi.showError(e.getMessage());
            }
        }
        return responseBuffer.toString(StandardCharsets.UTF_8)
                .replace(MESSAGE_DIVIDER, "")
                .strip();
    }

    /**
     * Returns the greeting shown when the GUI opens.
     *
     * @return greeting, including a storage warning when saved tasks could not be loaded
     */
    public String getGreeting() {
        String greeting = "Yo! I am Remy, your task-management sous-chef.\n"
                + "What can I help you remember today?\n\n"
                + "Try: todo, deadline, event, list, find, sort, mark, unmark, delete, or bye.";
        if (loadingWarning != null) {
            return greeting + "\n\n" + loadingWarning;
        }
        return greeting;
    }

    /**
     * Returns whether the user has ended this chat session.
     *
     * @return true after a bye command has been handled
     */
    public boolean hasExited() {
        return hasExited;
    }

    /** Executes one parsed command and updates the session's exit state. */
    private void executeCommand(String message, Ui ui) {
        Command command = parser.parse(message);
        assert command != null : "Parser must return a command or throw an exception";

        command.execute(tasks, ui, storage);
        hasExited = command.isExit();
    }
}
