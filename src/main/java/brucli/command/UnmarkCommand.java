package brucli.command;

import java.io.IOException;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.Ui;

/**
 * Reopens a completed task.
 */
public class UnmarkCommand extends TaskMutationCommand {

    /**
     * Creates a command targeting the supplied zero-based task ID.
     */
    public UnmarkCommand(int taskId) {
        super(taskId);
    }

    /**
     * Unmarks the task, saves the list, and displays confirmation.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.unmarkDone(taskId);
        saveTasks(tasks, storage);
        ui.showTaskUnmarked();
    }
}
