package sioet.command;

import sioet.storage.Storage;
import sioet.task.TaskList;
import sioet.ui.Ui;

/**
 * Represents the command that exits sioet.ui.Sioet.
 */
public class ExitCommand extends Command {

    /**
     * Executes the exit command.
     *
     * @param tasks   the task list
     * @param ui      the user interface
     * @param storage the task storage
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    /**
     * Returns whether this command should exit sioet.ui.Sioet.
     *
     * @return true because this is the exit command
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
