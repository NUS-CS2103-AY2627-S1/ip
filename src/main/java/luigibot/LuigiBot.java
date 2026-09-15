package luigibot;

import java.io.StringWriter;

import luigibot.command.Command;
import luigibot.parser.Parser;
import luigibot.storage.Storage;
import luigibot.task.TaskList;
import luigibot.ui.Ui;

/**
 * Runs LuigiBot's command-line task manager.
 */
public class LuigiBot {
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;
    private boolean isExitRequested;

    /**
     * Creates LuigiBot using the specified task-file path.
     *
     * @param filePath path used to load and save tasks.
     */
    public LuigiBot(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.parser = new Parser();
    }

    /**
     * Runs LuigiBot and processes commands until the user exits.
     */
    public void run() {
        this.ui.showGreeting();

        this.loadTasks(this.ui);

        boolean isExit = false;
        while (!isExit) {
            try {
                String userInput = this.ui.readCommand();
                isExit = this.executeCommand(userInput, this.ui);
            } catch (IllegalArgumentException exception) {
                this.ui.showError(exception.getMessage());
            }
        }

        this.ui.close();
    }

    /**
     * Processes one user command and returns LuigiBot's response.
     *
     * @param userInput full command entered by the user.
     * @return LuigiBot's response to the command.
     */
    public String getResponse(String userInput) {
        StringWriter response = new StringWriter();
        Ui responseUi = new Ui(response);
        this.loadTasks(responseUi);
        this.isExitRequested = false;

        try {
            this.isExitRequested = this.executeCommand(userInput, responseUi);
        } catch (IllegalArgumentException exception) {
            responseUi.showError(exception.getMessage());
        }
        return response.toString().stripTrailing();
    }

    /**
     * Indicates whether the most recently processed GUI command requests exit.
     *
     * @return true if the most recent command is an exit command.
     */
    public boolean isExitRequested() {
        return this.isExitRequested;
    }

    /**
     * Loads saved tasks the first time LuigiBot needs its task list.
     *
     * @param ui user interface used to report loading errors.
     */
    private void loadTasks(Ui ui) {
        if (this.tasks == null) {
            this.tasks = new TaskList(this.storage.load(ui));
        }
    }

    /**
     * Parses and executes one command.
     *
     * @param userInput full command entered by the user.
     * @param ui user interface used to display the command result.
     * @return true when the command requests exit.
     */
    private boolean executeCommand(String userInput, Ui ui) {
        Command command = this.parser.parse(userInput);
        command.execute(this.tasks, ui, this.storage);
        return command.isExit();
    }

    /**
     * Starts LuigiBot with its default task-file path.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new LuigiBot("data/luigibot.txt").run();
    }
}
