package echo;

import java.io.UncheckedIOException;
import java.nio.file.Paths;

import echo.command.Command;
import echo.command.CommandParser;
import echo.storage.Storage;
import echo.task.TaskList;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Provides the core chatbot logic and session manager for E.C.H.O.
 */
public class Echo {
    /** User interface for input and output. */
    private final Ui ui;
    /** Storage used to load and save the application's tasks. */
    private final Storage storage;
    /** Parser for converting user input into commands. */
    private final CommandParser commandParser;
    /** Manager for task operations and persistence. */
    private final TaskManager taskManager;
    /** Storage warning or notice encountered during loading. */
    private final String storageNotice;
    /** Flag indicating if an exit command has been received. */
    private boolean isExit;

    /**
     * Creates a new E.C.H.O. session using the default storage file.
     */
    public Echo() {
        this(new Storage());
    }

    /**
     * Creates a new E.C.H.O. session using a specific storage file.
     *
     * @param filePath file used to load and save tasks.
     */
    public Echo(String filePath) {
        this(new Storage(Paths.get(filePath)));
    }

    /**
     * Creates a new E.C.H.O. session using the supplied storage.
     *
     * @param storage storage used to load and save tasks.
     */
    public Echo(Storage storage) {
        this(new Ui(), storage);
    }

    /**
     * Creates a new E.C.H.O. session using explicit UI and storage dependencies.
     *
     * @param ui user interface used by the session.
     * @param storage storage used to load and save tasks.
     */
    public Echo(Ui ui, Storage storage) {
        this.ui = ui;
        this.storage = storage;
        this.commandParser = new CommandParser();

        String notice = null;
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
            if (storage.hasCorruptedEntries()) {
                notice = "[STORAGE NOTICE] Some corrupted entries in the archive could not be read and were skipped.";
            }
        } catch (UncheckedIOException exception) {
            ui.showLoadingError();
            tasks = new TaskList();
            notice = "[STORAGE FAULT] Failed to load directive archive. Initialized empty register.";
        }
        this.storageNotice = notice;
        this.taskManager = new TaskManager(tasks, this.storage);
    }

    /**
     * Starts an E.C.H.O. session in command-line mode.
     *
     * @param args command-line arguments, which are currently unused.
     */
    public static void main(String[] args) {
        new Echo().run();
    }

    /**
     * Generates a response for the user's chat message in GUI mode.
     *
     * @param input user chat input.
     * @return response message string.
     */
    public String getResponse(String input) {
        try {
            Command command = commandParser.parse(input);
            String response = command.execute(taskManager, ui);
            if (command.isExit()) {
                isExit = true;
            }
            return response;
        } catch (EchoException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Returns the welcome message for E.C.H.O.
     *
     * @return startup welcome message.
     */
    public String getWelcomeMessage() {
        String welcome = ui.getWelcomeMessage();
        if (storageNotice != null) {
            return welcome + "\n\n" + storageNotice;
        }
        return welcome;
    }

    /**
     * Returns whether the session has been terminated by an exit command.
     *
     * @return true if an exit command has been executed, false otherwise.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Main driver of E.C.H.O., reading CLI inputs from user until termination.
     */
    public void run() {
        ui.showWelcome();

        try {
            while (true) {
                String input = ui.readCommand();
                if (input == null) {
                    break;
                }

                if (input.isEmpty()) {
                    continue;
                }

                try {
                    Command command = commandParser.parse(input);
                    command.execute(taskManager, ui);
                    if (command.isExit()) {
                        isExit = true;
                        break;
                    }
                } catch (EchoException exception) {
                    ui.showError(exception.getMessage());
                }
            }
        } finally {
            ui.close();
        }
    }
}
