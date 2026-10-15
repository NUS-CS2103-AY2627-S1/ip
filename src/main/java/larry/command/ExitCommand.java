package larry.command;

import larry.storage.Storage;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Displays Larry's farewell message and signals that the application should terminate.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
