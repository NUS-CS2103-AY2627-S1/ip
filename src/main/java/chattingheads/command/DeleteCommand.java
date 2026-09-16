package chattingheads.command;

import chattingheads.exception.InvalidTaskNumberException;
import chattingheads.task.Task;
import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that deletes a task.
 */
public class DeleteCommand extends Command {

    private final int taskNumber;

    /**
     * Creates a command to delete the specified task.
     *
     * @param taskNumber Task number to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws InvalidTaskNumberException {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        Task deletedTask = taskList.delete(taskNumber - 1);
        return ui.getDeleteStatus(deletedTask, taskList);
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
