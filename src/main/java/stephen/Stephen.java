package stephen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import stephen.command.Command;
import stephen.exception.ChatbotException;
import stephen.parser.Parser;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Starts and coordinates the Orbit task-management chatbot. */
public class Stephen {
    private static final Path DATA_FILE = Path.of("data", "stephen.txt");

    /** Creates an application entry-point instance. */
    public Stephen() {
    }

    /**
     * Runs the chatbot and stores tasks entered during the current session.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String... args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE);
        Parser parser = new Parser();

        ui.showWelcome();
        TaskList tasks = new TaskList(loadTasks(storage, ui));
        runCommandLoop(ui, storage, parser, tasks);
    }

    /** Loads saved tasks, recovering with an empty list when the data cannot be read. */
    private static List<Task> loadTasks(Storage storage, Ui ui) {
        try {
            return storage.load();
        } catch (IOException e) {
            ui.showError("I couldn't load your tasks. Starting with an empty list.");
            ui.showDivider();
            return List.of();
        }
    }

    /** Reads and processes commands until input ends or the user exits. */
    private static void runCommandLoop(Ui ui, Storage storage, Parser parser, TaskList tasks) {
        while (ui.hasNextInput()) {
            if (!processNextCommand(ui, storage, parser, tasks)) {
                break;
            }
        }
    }

    /** Processes one command, returning whether the input loop should continue. */
    private static boolean processNextCommand(Ui ui, Storage storage, Parser parser,
            TaskList tasks) {
        Command command;
        try {
            command = parser.parse(ui.readCommand(), tasks);
        } catch (ChatbotException e) {
            showErrorBetweenDividers(ui, e.getMessage());
            return true;
        }

        if (!command.isExit()) {
            ui.showDivider();
        }
        executeCommand(command, tasks, ui, storage);
        if (!command.isExit()) {
            ui.showDivider();
        }
        return !command.isExit();
    }

    /** Executes a parsed command and displays any recoverable execution error. */
    private static void executeCommand(Command command, TaskList tasks, Ui ui, Storage storage) {
        try {
            command.execute(tasks, ui, storage);
        } catch (ChatbotException e) {
            ui.showError(e.getMessage());
        }
    }

    /** Displays a validation error using the console session's standard separators. */
    private static void showErrorBetweenDividers(Ui ui, String message) {
        ui.showDivider();
        ui.showError(message);
        ui.showDivider();
    }
}
