package sallman.command;

import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;

/**
 * Ends the session.
 */
public class ExitCommand extends Command {

    /**
     * Says goodbye. Nothing is changed, so nothing is saved.
     *
     * @param tasks   unused
     * @param ui      used to show the parting message
     * @param storage unused, since this command changes nothing
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Returns true: this is the command that ends the session.
     *
     * @return always true
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
