package eve.command;

import eve.Storage;
import eve.Ui;
import eve.task.TaskList;

/** Says goodbye and signals the main loop to stop. */
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
