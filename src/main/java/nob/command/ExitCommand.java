package nob.command;

import nob.storage.Storage;
import nob.task.TaskList;
import nob.ui.Ui;

/**
 * Displays Nob's farewell message and ends the application.
 */
public class ExitCommand extends Command {
    /** Displays Nob's farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }
}
