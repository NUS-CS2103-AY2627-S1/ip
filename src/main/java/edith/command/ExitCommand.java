package edith.command;

import edith.Storage;
import edith.Ui;
import edith.task.TaskList;

/** Ends the Edith session. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }
    @Override
    public boolean isExit() {
        return true;
    }
}
