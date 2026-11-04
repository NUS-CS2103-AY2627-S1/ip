package soar;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import soar.command.Command;
import soar.exception.SoarException;
import soar.exception.StorageException;
import soar.parser.Parser;
import soar.storage.Storage;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Starts the Soar chatbot application.
 */
public class Soar {
    /** Persistence service shared by commands in this session. */
    private final Storage storage;

    /** Task collection shared by commands in this session. */
    private final TaskList tasks;

    /** Startup error that blocks commands from overwriting unreadable task data. */
    private final String loadingError;

    /**
     * Creates a Soar session using the default task data file.
     */
    public Soar() {
        this(new Storage());
    }

    /** Creates a Soar session using the supplied persistence service. */
    Soar(Storage storage) {
        this.storage = Objects.requireNonNull(storage, "Storage must not be null");

        TaskList loadedTasks;
        String loadFailure = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (IOException | StorageException e) {
            loadedTasks = new TaskList();
            loadFailure = e.getMessage();
        }
        tasks = loadedTasks;
        loadingError = loadFailure;
    }

    /**
     * Greets the user, stores tasks, lists or updates their status on request, and
     * exits when the user enters {@code bye}.
     *
     * @param args Optional first argument overrides the default task data file.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage;
        try {
            storage = args.length == 0 ? new Storage() : new Storage(Path.of(args[0]));
        } catch (IllegalArgumentException e) {
            ui.showLoadingError(e.getMessage());
            return;
        }

        Soar soar = new Soar(storage);
        if (soar.loadingError != null) {
            ui.showLoadingError(soar.loadingError);
            return;
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            isExit = soar.execute(ui.readCommand(), ui);
        }
    }

    /**
     * Executes a user command and returns Soar's response without console framing.
     *
     * @param input Command entered by the user.
     * @return response text suitable for display in one GUI dialog box
     */
    public String getResponse(String input) {
        ArrayList<String> responseLines = new ArrayList<>();
        Ui responseUi = Ui.createResponseUi(responseLines::add);
        if (loadingError != null) {
            responseUi.showLoadingError(loadingError);
        } else {
            execute(input, responseUi);
        }
        return String.join("\n", responseLines);
    }

    /** Executes one command and reports whether it requests application exit. */
    private boolean execute(String input, Ui ui) {
        try {
            Command executableCommand = Parser.parse(input);
            executableCommand.execute(tasks, ui, storage);
            return executableCommand.isExit();
        } catch (SoarException e) {
            ui.showError(e.getMessage());
            return false;
        }
    }
}
