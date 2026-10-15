package minty.command;

import minty.exception.MintyException;
import minty.storage.Storage;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Represents input that does not match any command Minty understands.
 */
public class UnknownCommand extends Command {
    private final String message;

    /**
     * Creates the standard unrecognized-command response.
     */
    public UnknownCommand() {
        this("Whoops! I don't recognize that command. Try list to see your"
                + " tasks or todo read a book to add one.");
    }

    /**
     * Creates an actionable response for input that cannot be executed.
     *
     * @param message explanation and recovery hint.
     */
    public UnknownCommand(String message) {
        this.message = message;
    }

    /**
     * Reports that the input cannot be understood.
     *
     * @param tasks task list, which is not changed.
     * @param ui command-line interface, which is not used directly.
     * @param storage task storage, which is not used.
     * @throws MintyException always, because the command is unknown.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException {
        throw new MintyException(message);
    }
}
