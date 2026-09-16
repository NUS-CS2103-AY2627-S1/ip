package brucli.command;

import java.io.IOException;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.Ui;

/**
 * Marks a task as completed.
 */
public class MarkCommand extends TaskMutationCommand {

    /**
     * Creates a command targeting the supplied zero-based task ID.
     */
    public MarkCommand(int taskId) {
        super(taskId);
    }

    /**
     * Marks the task, saves the list, and displays confirmation.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.markDone(taskId);
        saveTasks(tasks, storage);
        ui.showTaskMarked();
    }
}
