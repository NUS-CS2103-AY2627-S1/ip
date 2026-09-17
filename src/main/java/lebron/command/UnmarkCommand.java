package lebron.command;

import lebron.exception.LeBronException;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "unmark" command, which marks a task as not done.
 */
public class UnmarkCommand implements Command {
    private final int taskNumber;

    /**
     * Constructs an UnmarkCommand for the given 1-based task number.
     *
     * @param taskNumber The 1-based task number to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LeBronException {
        Task task = taskList.getTask(taskNumber);
        task.setStatus(false);
        return ui.showTaskUnmarked(task);
    }
}
