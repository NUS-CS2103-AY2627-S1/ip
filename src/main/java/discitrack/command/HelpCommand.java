package discitrack.command;

import discitrack.storage.Storage;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Shows the commands supported by DisciTrack.
 */
public class HelpCommand extends Command {
    /**
     * Displays the command guide.
     *
     * @param tasks the task list used by the application.
     * @param ui the UI used to show the command guide.
     * @param storage the storage used by the application.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
