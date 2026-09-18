package friday.command;

import friday.exception.FridayException;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Displays tasks stored in the archive.
 */
public class ListArchiveCommand extends Command {
    /**
     * Displays all archived tasks in archive order.
     *
     * @param tasks Current active task list, which is unchanged.
     * @param archivedTasks Current archived task list to display.
     * @param ui UI used to display archived tasks.
     * @param storage Storage for active tasks, which is unused.
     * @param archiveStorage Storage for archived tasks.
     * @throws FridayException If archive storage is unavailable.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage,
            Storage archiveStorage) throws FridayException {
        if (archiveStorage == null) {
            throw new FridayException("Archive storage is unavailable. Please check the task data files.");
        }
        ui.showArchivedTaskList(archivedTasks);
    }
}
