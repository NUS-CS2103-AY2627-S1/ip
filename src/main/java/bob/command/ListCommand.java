package bob.command;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents a command to display all tasks in the task list.
 */
public class ListCommand extends Command {

    /**
     * Constructs a ListCommand.
     */
    public ListCommand() {
    }

    /**
     * Executes the list command by displaying all current tasks to the user.
     *
     * @param tasks   the list of tasks to display
     * @param ui      the user interface handler
     * @param storage the storage handler
     * @throws BobException if an error occurs during execution
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage<TaskList> storage) throws BobException {
        assertExecutionDependencies(tasks, ui, storage);
        ui.setTaskList(tasks);
    }
}
