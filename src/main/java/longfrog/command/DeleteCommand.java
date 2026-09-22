package longfrog.command;

import longfrog.task.Task;
import longfrog.task.TaskList;
import longfrog.ui.Ui;

/**
 * Removes a task selected by its zero-based position in the task list.
 */
public class DeleteCommand implements Command {
    private final TaskList taskList;
    private final int index;

    /**
     * Creates a delete command for a task position.
     *
     * @param taskList the list from which to remove a task
     * @param index the zero-based position to remove
     */
    public DeleteCommand(TaskList taskList, int index) {
        this.taskList = taskList;
        this.index = index;
    }

    /**
     * Removes the selected task and displays the outcome.
     */
    @Override
    public boolean execute(Ui ui) {
        Task deletedTask = taskList.removeTask(index);
        if (deletedTask == null) {
            ui.showInvalidTaskIndex(index, taskList.getCount(), "delete");
        } else {
            ui.showMessage("Released from the pond: " + deletedTask.getName());
        }

        return false;
    }
}
