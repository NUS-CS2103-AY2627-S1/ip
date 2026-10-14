package milo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import milo.command.Command;
import milo.exception.MiloException;
import milo.parser.Parser;
import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Coordinates Milo's storage, task list, parser, and console UI.
 */
public class Milo {
    private static final String DEFAULT_FILE_PATH = "data/duke.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /** Creates Milo using the default task data file. */
    public Milo() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates Milo and loads its saved tasks.
     *
     * @param filePath path of the task data file.
     */
    public Milo(String filePath) {
        this.storage = new Storage(filePath);
        this.ui = new Ui();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (MiloException exception) {
            ui.showError(exception.getMessage());
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
    }

    /** Starts Milo's command-reading loop. */
    public void run() {
        try {
            ui.showWelcome();
            boolean isExit = false;
            while (!isExit && ui.hasNextCommand()) {
                String fullCommand = ui.readCommand();
                ui.showLine();
                try {
                    isExit = executeCommand(fullCommand, ui);
                } finally {
                    ui.showLine();
                }
            }
        } finally {
            ui.close();
        }
    }

    /**
     * Processes one command and returns Milo's response for the graphical UI.
     *
     * @param input command entered by the user.
     * @return response produced by the command.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(
                responseBuffer, true, StandardCharsets.UTF_8)) {
            executeCommand(input.trim(), new Ui(responseOutput));
        }
        return responseBuffer.toString(StandardCharsets.UTF_8).stripIndent().strip();
    }

    /** Executes one command and reports recoverable errors through the supplied UI. */
    private boolean executeCommand(String fullCommand, Ui responseUi) {
        try {
            Command command = Parser.parse(fullCommand);
            command.execute(tasks, responseUi, storage);
            return command.isExit();
        } catch (MiloException exception) {
            responseUi.showError(exception.getMessage());
            return false;
        }
    }

    /**
     * Starts Milo using the default task data file.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Milo().run();
    }
}
