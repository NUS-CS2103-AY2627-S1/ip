package potatobot.logic.command;

import potatobot.logic.CommandResult;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

/**
 * Requests that PotatoBot reverse its most recent state-changing command.
 */
public class UndoCommand extends Command {
    /** Message shown when the current session has no command left to undo. */
    public static final String EMPTY_HISTORY_MESSAGE = "PotatoBot can't travel THAT far back in time sorry :(";
    private static final String SUCCESS_MESSAGE_FORMAT = "PototaBot has travelled back in time: %s undone";

    /**
     * Creates a command that requests an undo operation.
     */
    public UndoCommand() {
    }

    /**
     * Formats the response for a successfully reversed command.
     *
     * @param commandInput Original input for the reversed command.
     * @return Message describing the successful undo operation.
     */
    public static String formatSuccessMessage(String commandInput) {
        return SUCCESS_MESSAGE_FORMAT.formatted(commandInput);
    }

    /**
     * Returns the empty-history response when no application history is supplied.
     * PotatoBot handles this command directly when session history is available.
     *
     * @param tasks   Task list managed by the application.
     * @param storage Storage used by the application.
     * @return Result explaining that no command is available to undo.
     */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return CommandResult.error(EMPTY_HISTORY_MESSAGE);
    }
}
