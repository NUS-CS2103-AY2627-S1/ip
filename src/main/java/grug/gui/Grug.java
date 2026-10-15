package grug.gui;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.Optional;
import java.util.Scanner;

import grug.command.CommandResult;
import grug.command.GrugCommand;
import grug.command.parser.CommandParser;
import grug.command.parser.GrugCommandParserException;
import grug.storage.TaskStorage;
import grug.storage.serde.TaskDeserializerException;
import grug.task.TaskList;
import grug.ui.ConsoleUi;
import grug.ui.Ui;

/**
 * Exposes the API to control the Grug chatbot.
 */
public class Grug {
    public static final String DEFAULT_STORAGE_FILE = "./tasks.txt";
    private static final String DIALOG_SEP = "____________________________________________________________";
    private static final String BANNER = """
            ▄████  ██▀███   █    ██   ▄████
            ██▒ ▀█▒▓██ ▒ ██▒ ██  ▓██▒ ██▒ ▀█▒
            ▒██░▄▄▄░▓██ ░▄█ ▒▓██  ▒██░▒██░▄▄▄░
            ░▓█  ██▓▒██▀▀█▄  ▓▓█  ░██░░▓█  ██▓
            ░▒▓███▀▒░██▓ ▒██▒▒▒█████▓ ░▒▓███▀▒
            ░▒   ▒ ░ ▒▓ ░▒▓░░▒▓▒ ▒ ▒  ░▒   ▒
            ░   ░   ░▒ ░ ▒░░░▒░ ░ ░   ░   ░
            ░ ░   ░   ░░   ░  ░░░ ░ ░ ░ ░   ░
                ░    ░        ░           ░
            """.stripIndent();

    private Ui ui;
    private TaskList tasks;
    private TaskStorage storage;

    /**
     * Creates a new Grug chatbot instance which saves and loads tasks from the
     * {@code filepath}.
     *
     * @param filepath The filepath to save and load tasks data from.
     * @param ui       The {@link ConsoleUi} instance that specifies where to read
     *                 from
     *                 (e.g. {@code System.in}) and where to write to (e.g.
     *                 {@code System.out}).
     */
    public Grug(String filepath, Ui ui) {
        this.tasks = new TaskList();
        this.storage = new TaskStorage(filepath);
        this.ui = ui;
    }

    /**
     * Creates a new Grug chatbot which saves and loads tasks from
     * {@link #DEFAULT_STORAGE_FILE}, and with a {@link ConsoleUi} that reads from
     * {@code System.in} and outputs to {@code System.out}.
     */
    public Grug() {
        this(DEFAULT_STORAGE_FILE, new ConsoleUi(new Scanner(System.in), new PrintWriter(System.out)));
    }

    /**
     * Parses raw user input and maps it to the appropriate {@link GrugCommand}
     * variant.
     *
     * @param input the raw string input from the user's terminal.
     * @return the corresponding command variant.
     * @throws GrugCommandParserException if there was an error parsing the input.
     * @see CommandParser#parseInput(String)
     */
    public static GrugCommand parseInput(String input) throws GrugCommandParserException {
        return CommandParser.parseInput(input);
    }

    /**
     * Executes the command and may mutate the state of the instance's task list and
     * storage depending on the command.
     *
     * @param command The command to execute.
     * @return The result of executing the command, including the message and
     *         whether the program should exit.
     */
    public CommandResult execute(GrugCommand command) {
        return command.execute(tasks, storage);
    }

    /**
     * Reads the user's input after displaying the prompt.
     *
     * @param prompt The prompt to display.
     * @return The user's input, or an empty optional if no input is available.
     */
    public Optional<String> readUserInput(String prompt) {
        return this.ui.readInput(prompt);
    }

    /**
     * Converts the user's input to the appropriate {@link GrugCommand} and
     * executes the command.
     *
     * @return true if program should exit, false otherwise.
     */
    public boolean handleUserInput(String input) {
        GrugCommand command;
        try {
            command = CommandParser.parseInput(input);
        } catch (GrugCommandParserException e) {
            ui.displayError(e.getMessage());
            return false;
        }

        CommandResult result = command.execute(tasks, storage);

        switch (result) {
            case CommandResult.Ok(String message, boolean shouldExit) -> {
                ui.display(message);
                return shouldExit;
            }
            case CommandResult.Err(String message, boolean shouldExit) -> {
                ui.displayError(message);
                return shouldExit;
            }
            case CommandResult.Partial(String message, boolean shouldExit) -> {
                ui.displayWarning(message);
                return shouldExit;
            }
            default -> {
                // should never happen
                throw new IllegalStateException("Unknown CommandResult variant");
            }
        }
    }

    /**
     * Loads tasks from the storage and displays any errors.
     */
    public void loadTasks() {
        try {
            this.tasks = new TaskList(storage.loadTasks());
            ui.display("Loaded tasks from %s", storage.getFilepath());
        } catch (TaskDeserializerException e) {
            ui.displayWarning("Found malformed tasks file `%s`: %s"
                    .formatted(storage.getFilepath(), e.getMessage()));
            this.tryBackupTasksFile();
        } catch (NoSuchFileException e) {
            ui.display("No tasks to load: `%s` does not exist".formatted(storage.getFilepath()));
        } catch (AccessDeniedException e) {
            ui.displayError("Could not open tasks file `%s`: access denied".formatted(
                    storage.getFilepath()));
        } catch (IOException e) {
            ui.displayError("Could not open or read tasks file `%s`: %s".formatted(
                    storage.getFilepath(), e.getMessage()));
        }
    }

    private void tryBackupTasksFile() {
        String primaryFilepath = storage.getFilepath();

        try {
            String backupFilepath = storage.moveTasksFileToBackup();
            ui.displayWarning("Malformed tasks file `%s` was moved to `%s`".formatted(
                    primaryFilepath, backupFilepath));
        } catch (IOException backupException) {
            ui.displayError("Malformed tasks file `%s` could not be moved to a backup: %s".formatted(
                    primaryFilepath, backupException.getMessage()));
            ui.displayWarning("Tasks file `%s` will be overwritten. Use the `bye` command to cancel."
                    .formatted(primaryFilepath));
        }
    }

    /**
     * Displays the banner and greets the user.
     */
    public void greet() {
        ui.display(BANNER);
        ui.display("Unga. Me Grug. What do? ('bye' to quit)");
    }

    /**
     * Runs the Grug CLI program.
     */
    public void run() {
        this.loadTasks();
        this.greet();
        ui.display(DIALOG_SEP);

        // this repl is specific to the CLI so it lives here rather than as a method
        // inside Ui
        boolean isDone = false;
        while (!isDone) {
            Optional<String> inputOrEof = ui.readInput("> ");
            // EOF, e.g. ctrl+c, ctrl+z, etc.
            if (inputOrEof.isEmpty()) {
                isDone = true;
                continue;
            }

            String input = inputOrEof.get();
            boolean shouldExit = this.handleUserInput(input);
            ui.display(DIALOG_SEP);
            isDone = shouldExit;
        }
    }

    public static void main(String[] args) {
        new Grug().run();
    }
}
