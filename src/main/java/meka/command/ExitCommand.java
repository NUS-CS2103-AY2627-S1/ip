package meka.command;

import meka.storage.Storage;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Ends the current MEKA session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current session.
     */
    public ExitCommand() {
    }

    /**
     * Shows MEKA's goodbye message.
     *
     * @param tasks current task list; not modified.
     * @param ui user interface through which the message is displayed.
     * @param storage current storage component; not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that this command ends MEKA.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
