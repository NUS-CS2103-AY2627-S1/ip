package bingbong.command;

import bingbong.storage.Storage;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * bingbong.command.Command to handle terminating the chatbot application.
 */
public class ExitCommand extends Command {

    /**
     * Prints exit strings.
     *
     * @param tasks   The active TaskList tracking current items.
     * @param ui      The user interface formatting output blocks.
     * @param storage The storage file handler managing disk reads and writes.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    /**
     * Overrides state signals to acknowledge termination loops inside global application scopes.
     *
     * @return True always to ensure thread execution wraps up cleanly.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
