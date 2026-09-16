package chattingheads.command;

import chattingheads.exception.InvalidTaskNumberException;
import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that marks a task as complete.
 */
public class MarkCommand extends Command {

    private final int taskNumber;

    /**
     * Creates a command to mark the specified task as complete.
     *
     * @param taskNumber Task number to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws InvalidTaskNumberException {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        taskList.mark(taskNumber - 1);
        return ui.getMarkStatus(taskList.get(taskNumber - 1));
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
