package nori.command;

import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Ends Nori's command loop.
 */
class ExitCommand extends Command {
    /** Creates an exit command. */
    ExitCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Nori ends its loop after this command has executed.
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExit() {
        return true;
    }
}
