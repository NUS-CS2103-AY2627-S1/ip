package lebron.command;

import lebron.exception.LeBronException;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "delete" command, which removes a task from the TaskList.
 */
public class DeleteCommand implements Command {
    private final int taskNumber;

    /**
     * Constructs a DeleteCommand for the given 1-based task number.
     *
     * @param taskNumber The 1-based task number to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LeBronException {
        Task task = taskList.getTask(taskNumber);
        taskList.deleteTask(taskNumber);
        return ui.showTaskDeleted(task, taskList.size());
    }
}
