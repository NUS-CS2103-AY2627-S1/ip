package clover.command;

import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Ends the Clover application after displaying a farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Displays the farewell message and closes console input.
     */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) {
        ui.showGoodbye();
        ui.close();
    }

    /**
     * Indicates that Clover should leave its command loop.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
