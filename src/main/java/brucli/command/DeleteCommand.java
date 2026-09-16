package brucli.command;

import java.io.IOException;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends TaskMutationCommand {

    /**
     * Creates a command targeting the supplied zero-based task ID.
     */
    public DeleteCommand(int taskId) {
        super(taskId);
    }

    /**
     * Deletes the task, saves the reindexed list, and displays confirmation.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.delete(taskId);
        saveTasks(tasks, storage);
        ui.showTaskDeleted();
    }
}
