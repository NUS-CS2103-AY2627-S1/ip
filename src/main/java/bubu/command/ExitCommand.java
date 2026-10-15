package bubu.command;

import bubu.storage.Storage;
import bubu.task.TaskList;
import bubu.ui.Ui;

/**
 * Displays a farewell message and ends the chatbot loop.
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
