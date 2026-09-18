package toothless.command;

import toothless.exception.ToothlessException;
import toothless.storage.Storage;
import toothless.task.Task;
import toothless.task.TaskList;
import toothless.task.TaskUpdate;
import toothless.ui.Ui;

/**
 * Coordinates replacing selected fields of one existing task.
 */
public class EditCommand extends Command {
    private final int taskNumber;
    private final TaskUpdate update;

    /**
     * Creates an edit command for a parsed task number and replacement fields.
     *
     * @param taskNumber one-based task number obtained from the parser.
     * @param update validated replacement fields obtained from the parser.
     */
    public EditCommand(int taskNumber, TaskUpdate update) {
        this.taskNumber = taskNumber;
        this.update = update;
    }

    /**
     * Applies the requested fields and displays the updated task.
     *
     * @param taskList list containing the task selected for editing.
     * @param ui user interface used to confirm the edit.
     * @param storage storage available to mutating commands.
     * @throws ToothlessException if the task or requested fields are invalid
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage)
            throws ToothlessException {
        TaskList proposedTasks = taskList.copy();
        Task updatedTask = proposedTasks.editTask(taskNumber, update);
        if (saveTasks(taskList, proposedTasks, ui, storage)) {
            ui.showTaskEdited(updatedTask);
        }
    }
}
