package toothless.command;

import toothless.exception.ToothlessException;
import toothless.storage.Storage;
import toothless.task.Task;
import toothless.task.TaskList;
import toothless.ui.Ui;

/**
 * Coordinates marking one task and persisting the updated task list.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a mark command for a parsed one-based task number.
     *
     * @param taskNumber one-based task number obtained from the parser.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task, displays it, and saves the updated task list.
     *
     * @param taskList list containing the task selected for completion
     * @param ui user interface used to confirm the change or report a save failure
     * @param storage storage used to persist the updated task list
     * @throws ToothlessException if the selected task does not exist
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage)
            throws ToothlessException {
        TaskList proposedTasks = taskList.copy();
        if (proposedTasks.isTaskDone(taskNumber)) {
            ui.showTaskAlreadyMarked(proposedTasks.getTask(taskNumber - 1));
            return;
        }
        Task markedTask = proposedTasks.markTask(taskNumber);
        if (saveTasks(taskList, proposedTasks, ui, storage)) {
            ui.showTaskMarked(markedTask);
        }
    }
}
