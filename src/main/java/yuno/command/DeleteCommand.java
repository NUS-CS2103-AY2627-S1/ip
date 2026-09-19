package yuno.command;

import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that deletes a task.
 */
public class DeleteCommand extends Command {
    /**
     * Creates a command with the index of a task to delete.
     *
     * @param taskNumber One-based task number entered by the user.
     */
    public DeleteCommand(String taskNumber) {
        super(taskNumber);
    }

    /**
     * Deletes the specified task and displays its confirmation.
     *
     * @param taskList Task list that contains the task.
     * @param ui User interface used to display the confirmation.
     * @param storage Storage used to save the updated task list.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If the task number is invalid or the updated task list cannot be saved.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        int taskNumber = parseTaskNumber();
        TaskList updatedTaskList = taskList.copy();
        Task deletedTask = updatedTaskList.deleteTask(taskNumber);
        storage.save(updatedTaskList);
        taskList.replaceWith(updatedTaskList);
        ui.printDeleteTask(deletedTask);
        return CommandResult.CONTINUE;
    }
}
