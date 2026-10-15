package shai.command;

import shai.storage.Storage;
import shai.task.TaskList;
import shai.ui.Ui;

/**
 * A command that displays the goodbye message and ends the application.
 */
public class ExitCommand extends Command {
    /** Displays the goodbye response. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Indicates that the command loop should stop after execution. */
    @Override
    public boolean isExit() {
        return true;
    }
}
