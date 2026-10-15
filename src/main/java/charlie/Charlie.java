package charlie;

import charlie.command.Command;
import charlie.exception.CharlieException;
import charlie.parser.Parser;
import charlie.storage.Storage;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    /** Greeting shown when the graphical interface opens. */
    private static final String GUI_GREETING =
            "Oh, hello! I'm Charlie.\nReady to tackle some tasks together?";

    /** Saves and loads Charlie's task data. */
    private Storage storage;

    /** Handles console input and output. */
    private Ui ui;

    /** Stores the tasks in the current session. */
    private TaskList taskList;

    /** Indicates whether saved tasks have been loaded for this session. */
    private boolean areTasksLoaded;

    /** Indicates whether the latest command requested that Charlie exit. */
    private boolean isExitRequested;

    /**
     * Creates Charlie with collaborators for user interaction and task persistence.
     *
     * @param filePath Path to the saved-task file.
     */
    public Charlie(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.taskList = new TaskList();
        this.areTasksLoaded = false;
        this.isExitRequested = false;
    }

    /**
     * Starts Charlie using the default save-file location.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Charlie("data/charlie.txt").run();
    }

    /**
     * Loads saved tasks and processes commands until the user exits.
     */
    public void run() {
        try {
            ui.showIntro();
            String loadingError = loadTasks();
            if (loadingError != null) {
                ui.showLoadingError(loadingError);
            }
            readCommands();
        } finally {
            ui.close();
        }
    }

    /**
     * Returns the greeting and any saved-task loading error for the graphical interface.
     *
     * @return Startup message to display in the conversation.
     */
    public String getGreeting() {
        String loadingError = loadTasks();
        if (loadingError == null) {
            return GUI_GREETING;
        }
        return GUI_GREETING + "\nError loading saved tasks: " + loadingError;
    }

    /**
     * Executes one user command and returns Charlie's response for the graphical interface.
     *
     * @param input Command entered by the user.
     * @return Charlie's response, with separate output lines joined by line breaks.
     */
    public String getResponse(String input) {
        loadTasks();
        StringBuilder response = new StringBuilder();
        Ui responseUi = new Ui(message -> appendResponseLine(response, message));

        try {
            Command command = Parser.parse(input);
            command.execute(taskList, responseUi, storage);
            isExitRequested = command.isExit();
        } catch (CharlieException e) {
            responseUi.showMessage(e.getMessage());
            isExitRequested = false;
        }
        return response.toString();
    }

    /**
     * Returns whether the latest command requested that the application exit.
     *
     * @return True after a successful {@code bye} command.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Loads saved tasks once and returns any friendly loading error.
     *
     * @return Loading error message, or {@code null} when loading succeeds.
     */
    private String loadTasks() {
        if (areTasksLoaded) {
            return null;
        }
        areTasksLoaded = true;
        try {
            taskList = new TaskList(storage.load());
            return null;
        } catch (CharlieException e) {
            taskList = new TaskList();
            return e.getMessage();
        }
    }

    /**
     * Appends one output line to a graphical-interface response.
     *
     * @param response Response being assembled.
     * @param line Line to append.
     */
    private void appendResponseLine(StringBuilder response, String line) {
        if (!response.isEmpty()) {
            response.append(System.lineSeparator());
        }
        response.append(line);
    }

    /**
     * Reads commands until the user enters {@code bye}.
     * Each recognized command performs its requested task-list operation.
     */
    private void readCommands() {
        while (ui.hasNextCommand()) {
            try {
                String input = ui.readCommand();
                ui.showHorizontalLine();
                Command command = Parser.parse(input);
                command.execute(taskList, ui, storage);
                if (command.isExit()) {
                    break;
                }
            } catch (CharlieException e) {
                ui.showMessage(e.getMessage());
            }
            ui.showHorizontalLine();
        }
    }
}
