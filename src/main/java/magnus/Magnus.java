package magnus;

import java.nio.file.Path;
import java.util.Scanner;

import magnus.command.CommandResult;
import magnus.command.CommandRouter;
import magnus.command.CommandType;
import magnus.exception.MagnusException;
import magnus.exception.StorageException;
import magnus.storage.Storage;
import magnus.task.TaskList;
import magnus.ui.Ui;

/**
 * Starts Magnus and coordinates its user interface, commands, and persistent task storage.
 */
public class Magnus {
    private static final Path DATA_FILE_PATH = Path.of("data", "magnus.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final CommandRouter router;
    private boolean isExitRequested;

    /**
     * Creates a Magnus application using the default task data file.
     *
     * @throws MagnusException If the saved tasks cannot be loaded.
     */
    public Magnus() throws MagnusException {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a Magnus application using the specified task data file.
     *
     * @param dataFilePath Path used to load and save tasks.
     * @throws MagnusException If the saved tasks cannot be loaded.
     */
    public Magnus(Path dataFilePath) throws MagnusException {
        this.storage = new Storage(dataFilePath);
        this.tasks = new TaskList(this.storage.loadTasks());
        this.router = new CommandRouter(this.tasks);
        this.isExitRequested = false;
    }

    /**
     * Processes one user command and returns its response message.
     * Task changes are saved before a successful response is returned.
     *
     * @param userInput The raw command entered by the user.
     * @return The command result message, or an error message if the command fails.
     */
    public String getResponse(String userInput) {
        return getResponseResult(userInput).message();
    }

    /**
     * Processes one user command and returns both its message and error state.
     * This richer result lets graphical interfaces distinguish errors without
     * inferring their meaning from user-facing text.
     *
     * @param userInput The raw command entered by the user.
     * @return The response text and whether command processing failed.
     */
    public MagnusResponse getResponseResult(String userInput) {
        this.isExitRequested = false;
        TaskList.Snapshot taskSnapshot = this.tasks.createSnapshot();

        try {
            CommandResult commandResult = this.router.route(userInput);
            CommandType commandType = commandResult.commandType();
            if (commandResult.hasTaskListChanged()) {
                this.storage.saveTasks(this.tasks.getTasks());
            }
            this.isExitRequested = commandType == CommandType.BYE;
            return MagnusResponse.createSuccess(commandResult.message());
        } catch (StorageException exception) {
            this.tasks.restoreSnapshot(taskSnapshot);
            return MagnusResponse.createError(exception.getMessage()
                    + "\n\tThe move was rolled back, so your in-memory task list is unchanged.");
        } catch (MagnusException exception) {
            return MagnusResponse.createError(exception.getMessage());
        }
    }

    /**
     * Returns whether the latest successfully processed command requested an exit.
     *
     * @return {@code true} if the latest command was {@code bye}; otherwise {@code false}.
     */
    public boolean isExitRequested() {
        return this.isExitRequested;
    }

    /**
     * Runs Magnus's command-line interaction loop.
     *
     * @param ignoredArgs Command-line arguments, which are not used.
     */
    public static void main(String[] ignoredArgs) {
        Ui ui = new Ui();
        ui.showWelcome();

        Magnus magnus;
        try {
            magnus = new Magnus();
        } catch (MagnusException exception) {
            ui.showMessage(exception.getMessage());
            return;
        }

        runCommandLoop(magnus, ui);
        ui.printDivider();
    }

    /**
     * Reads and processes commands from standard input until input ends or Magnus exits.
     *
     * @param magnus The command-processing application.
     * @param ui The command-line user interface.
     */
    private static void runCommandLoop(Magnus magnus, Ui ui) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();
                ui.printDivider();
                ui.showMessage(magnus.getResponse(userInput));

                if (magnus.isExitRequested()) {
                    break;
                }
                ui.printDivider();
            }
        }
    }
}
