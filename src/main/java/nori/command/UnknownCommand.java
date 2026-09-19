package nori.command;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Reports input Nori will not act on, and why.
 *
 * The explanation is supplied rather than fixed, so that input refused for
 * different reasons -- an unrecognized keyword, a command far too long, a
 * keyword misspelt by one letter -- can each say what is actually wrong.
 */
class UnknownCommand extends Command {
    /** What to tell the user about the input that was refused. */
    private final String message;

    /**
     * Creates a command that refuses some input.
     *
     * @param message the explanation and correction to show the user.
     */
    UnknownCommand(String message) {
        this.message = message;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        throw new NoriException(message);
    }
}
