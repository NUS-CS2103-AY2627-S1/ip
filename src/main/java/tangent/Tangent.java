package tangent;

import java.util.Scanner;

import tangent.command.Command;
import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;
import tangent.parser.Parser;
import tangent.storage.Storage;
import tangent.task.TaskList;
import tangent.ui.Ui;

/** Coordinates Tangent's UI, parsing, task list, and storage. */
public class Tangent {
    /** A storage object that handles all interactions with the data file. */
    private final Storage storage;
    /** A user interface object that handles all console responses. */
    private final Ui ui;
    /** The task list shared by console and GUI command execution. */
    private TaskList tasks;
    /** Whether most recent command requested application exit. */
    private boolean isExitRequested;

    /** Creates a new instance of Tangent using the supplied file path. */
    public Tangent(String filePath) {
        this.storage = new Storage(filePath);
        this.ui = new Ui();
    }

    /** Runs Tangent's console command loop. */
    public void run() {
        ui.showWelcome();
        try {
            tasks = loadTasks();
        } catch (TangentException e) {
            ui.showError(e.getMessage());
            return;
        }
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String input = ui.readCommand(scanner);
                ui.showDivider();
                if (processCommand(input)) {
                    return;
                }
            }
        }
    }

    /** Processes one console command and returns whether the application should exit. */
    private boolean processCommand(String input) {
        if (input.isEmpty()) {
            ui.showError(ErrorMessages.EMPTY_INPUT_MESSAGE);
            ui.showDivider();
            return false;
        }
        try {
            Command command = Parser.parse(input);
            executeCommand(command, ui);
            ui.showDivider();
            return command.isExit();
        } catch (TangentException e) {
            ui.showError(e.getMessage());
            ui.showDivider();
            return false;
        }
    }

    /** Starts Tangent with its default task data file. */
    public static void main(String[] args) {
        new Tangent("data/tangent.txt").run();
    }

    /** Returns whether GUI should close after the most recent command. */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /** Executes a parsed command with shared application state. */
    private void executeCommand(Command command, Ui commandUi) throws TangentException {
        command.execute(tasks, commandUi, storage);
    }

    /** Executes one command using the supplied UI output handler. */
    public void executeCommand(String input, Ui commandUi) {
        assert input != null : "command input must exist";
        assert commandUi != null : "command UI must exist";
        try {
            if (tasks == null) {
                tasks = loadTasks();
            }
            if (input.isBlank()) {
                commandUi.showError(ErrorMessages.EMPTY_INPUT_MESSAGE);
                return;
            }
            Command command = Parser.parse(input.trim());
            executeCommand(command, commandUi);
            isExitRequested = command.isExit();
        } catch (TangentException e) {
            commandUi.showError(e.getMessage());
        }
    }

    /** Loads the saved tasks into a new task list. */
    private TaskList loadTasks() throws TangentException {
        return new TaskList(storage.load());
    }
}
