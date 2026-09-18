package meka.command;

import java.io.IOException;

import meka.storage.Storage;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Archives every active task and leaves the user with an empty task list.
 */
public class ArchiveCommand extends Command {
    /**
     * Creates a command that archives all active tasks.
     */
    public ArchiveCommand() {
    }

    /**
     * Archives and removes every active task after persistence succeeds.
     *
     * @param tasks active tasks to archive.
     * @param ui user interface through which the result is displayed.
     * @param storage storage component used to persist active and archived tasks.
     * @throws IOException if the archive or active-task file cannot be updated.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        int taskCount = tasks.size();
        if (taskCount == 0) {
            ui.showNoTasksToArchive();
            return;
        }

        storage.archiveAll(tasks);
        tasks.clear();
        ui.showTasksArchived(taskCount);
    }
}
