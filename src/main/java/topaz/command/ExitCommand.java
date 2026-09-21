package topaz.command;

import topaz.storage.Storage;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Ends the chatbot session.
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
