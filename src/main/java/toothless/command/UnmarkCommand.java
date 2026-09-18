package toothless.command;

import toothless.exception.ToothlessException;
import toothless.storage.Storage;
import toothless.task.TaskList;
import toothless.ui.Ui;

/**
 * Coordinates unmarking one task and persisting the updated task list.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates an unmark command for a parsed one-based task number.
     *
     * @param taskNumber one-based task number obtained from the parser.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks the selected task, displays the result, and saves changed state.
     *
     * @param taskList list containing the task selected for unmarking
     * @param ui user interface used to display the result or report a save failure
     * @param storage storage used to persist the updated task list when it changes
     * @throws ToothlessException if the selected task does not exist
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage)
            throws ToothlessException {
        TaskList proposedTasks = taskList.copy();
        TaskList.UnmarkResult result = proposedTasks.unmarkTask(taskNumber);
        if (!result.wasChanged()) {
            ui.showTaskAlreadyUnmarked(result.getTask());
            return;
        }

        if (saveTasks(taskList, proposedTasks, ui, storage)) {
            ui.showTaskUnmarked(result.getTask());
        }
    }
}
