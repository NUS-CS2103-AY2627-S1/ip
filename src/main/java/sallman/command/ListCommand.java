package sallman.command;

import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;

/**
 * Shows every task in the list.
 */
public class ListCommand extends Command {

    /**
     * Shows every task. Nothing is changed, so nothing is saved.
     *
     * @param tasks   the list to show
     * @param ui      used to show it
     * @param storage unused, since this command changes nothing
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
