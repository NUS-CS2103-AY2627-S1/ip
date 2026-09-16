package discitrack.command;

import discitrack.storage.Storage;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Ends the DisciTrack application.
 */
public class ByeCommand extends Command {
    /**
     * Shows the farewell message.
     *
     * @param tasks the task list used by the application.
     * @param ui the UI used to show the farewell message.
     * @param storage the storage used by the application.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    /**
     * Returns true because this command exits the application.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
