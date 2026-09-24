package aravii;

import java.io.IOException;

/**
 * Coordinates commands and safe autosaving consistently for the CLI and GUI.
 */
public class ChatSession {
    private final Storage storage;

    private final CommandHandler commandHandler = new CommandHandler();

    private TaskList tasks;

    private String startupError = "";

    private boolean hasUnsavedChanges;

    private boolean shouldExit;

    /**
     * Loads saved tasks and blocks task commands if loading fails.
     *
     * @param storage The storage used by this session.
     */
    public ChatSession(Storage storage) {
        this.storage = storage;
        try {
            tasks = storage.load();
        } catch (IOException exception) {
            tasks = new TaskList();
            startupError = "Error: Could not load saved tasks. " + exception.getMessage()
                    + " The original file has not been changed. Back up and repair data/aravii.txt,"
                    + " then restart. Only help and bye are available.";
        }
    }

    /**
     * Returns the startup warning, or an empty string after a successful load.
     *
     * @return The user-facing load error.
     */
    public String getStartupError() {
        return startupError;
    }

    /**
     * Returns whether bye completed without leaving unsaved changes.
     *
     * @return Whether the interface can close normally.
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Handles one command, including user-visible validation and storage errors.
     * An unsuccessful save retains changes in memory and prevents a normal exit.
     *
     * @param input The complete command line.
     * @return The response to display in either interface.
     */
    public String respond(String input) {
        try {
            Parser.Command command = Parser.parse(input);
            if (!startupError.isEmpty() && !command.name().equals("help") && !command.name().equals("bye")) {
                return startupError;
            }
            if (command.name().equals("bye")) {
                savePendingChanges();
                shouldExit = true;
                return commandHandler.execute(tasks, command);
            }
            String response = commandHandler.execute(tasks, command);
            if (command.isMutation()) {
                hasUnsavedChanges = true;
                try {
                    savePendingChanges();
                } catch (IOException exception) {
                    return response + "\n" + saveError(exception);
                }
            }
            return response;
        } catch (IllegalArgumentException exception) {
            return "Error: " + exception.getMessage();
        } catch (IOException exception) {
            return saveError(exception);
        }
    }

    private void savePendingChanges() throws IOException {
        if (hasUnsavedChanges) {
            storage.save(tasks);
            hasUnsavedChanges = false;
        }
    }

    private String saveError(IOException exception) {
        return "Error: Could not save tasks. " + exception.getMessage()
                + " Changes are only in memory. Fix the save path and enter bye to retry; do not close the app.";
    }
}
