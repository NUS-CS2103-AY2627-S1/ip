package yuki.command;

import yuki.storage.Storage;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Ends the current Yuki session.
 */
public class ExitCommand extends Command {
    /** Displays Yuki's farewell. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Indicates that Yuki should stop after this command. */
    @Override
    public boolean isExit() {
        return true;
    }
}
