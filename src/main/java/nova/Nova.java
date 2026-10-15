package nova;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

import nova.command.Command;
import nova.command.ExitCommand;
import nova.command.HelpCommand;
import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "nova.txt");

    private final Storage storage;
    private final Ui ui;
    private final Parser parser;
    private TaskList tasks;
    private boolean isExitRequested;
    // Keeps the original file protected for the entire session after a failed load.
    private boolean hasLoadFailed;

    /**
     * Creates Nova using its default data file and console collaborators.
     */
    public Nova() {
        this(new Storage(DEFAULT_FILE_PATH), new Ui(), new Parser());
    }

    /**
     * Creates a Nova application with its collaborating components.
     *
     * @param storage storage used to load and save tasks.
     * @param ui UI used for console interaction.
     * @param parser parser used to interpret commands.
     */
    public Nova(Storage storage, Ui ui, Parser parser) {
        this.storage = storage;
        this.ui = ui;
        this.parser = parser;
    }

    /**
     * Greets the user, stores tasks, lists stored tasks, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Nova().run();
    }

    /**
     * Starts Nova's command loop.
     */
    public void run() {
        ui.showWelcome();
        initializeTasks(ui);

        while (ui.hasNextCommand()) {
            ui.showDivider();
            executeCommand(ui.readCommand(), ui);
            ui.showDivider();
            if (isExitRequested) {
                break;
            }
        }
    }

    /**
     * Starts a GUI session and returns Nova's initial message.
     *
     * @return greeting and any storage warning generated during startup
     */
    public String startGui() {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui guiUi = createResponseUi(outputBytes);
        guiUi.showGuiWelcome();
        initializeTasks(guiUi);
        return outputBytes.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Executes one GUI command using the same parser, commands, task list, and storage as the console UI.
     *
     * @param input command entered by the user
     * @return Nova's response to the command
     */
    public String getResponse(String input) {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui responseUi = createResponseUi(outputBytes);
        initializeTasks(responseUi);
        executeCommand(input, responseUi);
        return outputBytes.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Returns whether the most recent command ended the current session.
     *
     * @return {@code true} after a successful bye command
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Loads saved tasks once and reports recoverable loading problems through the given UI.
     */
    private void initializeTasks(Ui outputUi) {
        if (tasks != null) {
            return;
        }
        try {
            tasks = new TaskList(storage.load(), storage);
            if (storage.getSkippedRecordCount() > 0) {
                outputUi.showSkippedRecords(storage.getSkippedRecordCount());
            }
        } catch (IOException exception) {
            hasLoadFailed = true;
            outputUi.showError("I could not load your tasks from the data file.");
            tasks = new TaskList(new ArrayList<>(), storage);
        }
    }

    /**
     * Executes one command and reports parsing or storage errors through the given UI.
     */
    private void executeCommand(String input, Ui outputUi) {
        assert tasks != null : "Task list must be initialized before executing commands";

        isExitRequested = false;
        try {
            Command command = parser.parse(input);
            if (hasLoadFailed && !(command instanceof HelpCommand) && !(command instanceof ExitCommand)) {
                throw new NovaException("I could not load your tasks. Please fix the data file and restart Nova. "
                        + "Only help and bye are available this session.");
            }
            command.execute(tasks, outputUi);
            isExitRequested = command.isExit();
        } catch (NovaException exception) {
            outputUi.showError(exception.getMessage());
        } catch (IOException exception) {
            outputUi.showError("I could not save your tasks to the data file.");
        }
    }

    /**
     * Returns a UI that captures one response in memory.
     */
    private Ui createResponseUi(ByteArrayOutputStream outputBytes) {
        PrintStream output = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);
        return new Ui(InputStream.nullInputStream(), output);
    }
}
