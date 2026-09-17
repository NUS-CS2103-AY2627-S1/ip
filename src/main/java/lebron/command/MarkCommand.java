package lebron.command;

import lebron.exception.LeBronException;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "mark" command, which marks a task as done.
 */
public class MarkCommand implements Command {
    private final int taskNumber;

    /**
     * Constructs a MarkCommand for the given 1-based task number.
     *
     * @param taskNumber The 1-based task number to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LeBronException {
        Task task = taskList.getTask(taskNumber);
        task.setStatus(true);
        return ui.showTaskMarked(task);
    }
}
