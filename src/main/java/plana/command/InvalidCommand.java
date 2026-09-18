package plana.command;

import plana.exception.PlanaException;
import plana.storage.Storage;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Represents input that cannot be executed because it is not a recognized
 * Plana command.
 */
public class InvalidCommand extends Command {
    private final String errorMessage;

    /**
     * Creates an invalid command with its user-facing explanation.
     *
     * @param errorMessage the error to display when execution is attempted.
     */
    public InvalidCommand(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Reports why the input could not be executed.
     *
     * @param tasks unused by this command.
     * @param ui unused by this command.
     * @param storage unused by this command.
     * @throws PlanaException always, with the stored user-facing message
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PlanaException {
        throw new PlanaException(errorMessage);
    }
}
