package luna;

import java.io.IOException;
import java.util.List;

import luna.command.Command;
import luna.command.HelpCommand;
import luna.parser.Parser;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Runs Luna, a simple command-line task manager chatbot.
 */
public class Luna {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private boolean isExitRequested;
    private boolean isLatestResponseError;
    private boolean isLatestResponseHelp;
    private boolean isStorageReady;

    /**
     * Creates a Luna application with its UI, storage, and task list.
     */
    public Luna() {
        this(new Ui(), new Storage());
    }

    /**
     * Creates a Luna application with the given collaborators.
     *
     * @param ui User interface used to format and display responses.
     * @param storage Storage used to load and save tasks.
     */
    public Luna(Ui ui, Storage storage) {
        this.ui = ui;
        this.storage = storage;
        this.isLatestResponseError = false;
        this.isLatestResponseHelp = false;
        this.isStorageReady = false;
        this.tasks = loadTasks();
        this.isExitRequested = false;
    }

    /**
     * Runs the main command loop of the application.
     */
    public void run() {
        ui.showWelcome();

        while (!isExitRequested) {
            String input = ui.readCommand();

            if (input.isEmpty()) {
                continue;
            }
            getResponse(input);
        }
        ui.close();
    }

    /**
     * Returns the welcome message shown when the application starts.
     *
     * @return Welcome message with the command summary.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Processes one user command and returns Luna's response text.
     *
     * @param input Raw user command.
     * @return Response generated for the command.
     */
    public String getResponse(String input) {
        String trimmedInput = input == null ? "" : input.trim();
        isLatestResponseError = false;
        isLatestResponseHelp = false;
        if (trimmedInput.isEmpty()) {
            return "";
        }

        try {
            Command command = Parser.parse(trimmedInput);
            if (!isStorageReady && command.isMutating()) {
                throw new LunaException("Changes are disabled because your saved task file could not be loaded. "
                        + "Fix or restore the file, then restart Luna so your data is not overwritten.");
            }
            command.execute(tasks, ui, storage);
            isLatestResponseHelp = command instanceof HelpCommand;
            isExitRequested = command.isExit();
        } catch (LunaException e) {
            isLatestResponseError = true;
            ui.showError(e.getMessage());
        } catch (NumberFormatException e) {
            isLatestResponseError = true;
            ui.showError("Please provide a valid integer for task number.");
        } catch (IndexOutOfBoundsException e) {
            isLatestResponseError = true;
            ui.showError("That task number does not exist in your list.");
        } catch (IllegalArgumentException e) {
            isLatestResponseError = true;
            ui.showError(e.getMessage());
        }

        return ui.consumeLatestResponse();
    }

    /**
     * Returns whether Luna has already received an exit command.
     *
     * @return {@code true} if the application should stop accepting commands.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns whether the most recently generated response reports an error.
     *
     * @return {@code true} if the latest response should use the error presentation.
     */
    public boolean isLatestResponseError() {
        return isLatestResponseError;
    }

    /**
     * Returns whether the most recent response contains the command reference.
     *
     * @return {@code true} if the latest response should use the help presentation.
     */
    public boolean isLatestResponseHelp() {
        return isLatestResponseHelp;
    }

    /**
     * Returns any pending startup message that has not been consumed yet.
     *
     * @return Pending response text, or an empty string if none exists.
     */
    public String consumePendingResponse() {
        return ui.consumeLatestResponse();
    }

    /**
     * Starts the Luna application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        new Luna().run();
    }

    /**
     * Loads tasks from storage and enables a protected read-only session if loading fails.
     *
     * @return Task list initialized from saved data when available.
     */
    private TaskList loadTasks() {
        try {
            List<Task> loadedTasks = storage.loadTasks();
            assert loadedTasks != null : "Storage must return a non-null task collection";
            isStorageReady = true;
            return new TaskList(loadedTasks);
        } catch (IOException | LunaException | SecurityException e) {
            isLatestResponseError = true;
            ui.showLoadingError();
            return new TaskList();
        }
    }
}
