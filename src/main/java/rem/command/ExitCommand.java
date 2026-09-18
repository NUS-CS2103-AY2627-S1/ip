package rem.command;

import rem.storage.Storage;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Ends the current Rem session.
 */
public class ExitCommand extends Command {
    /**
     * Displays Rem's farewell message.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the farewell.
     * @param storage Storage available to commands that need persistence.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("[Yawn] Need more sleep. Time for bed...");
    }

    /**
     * Indicates that this command ends the application.
     *
     * @return Always true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
