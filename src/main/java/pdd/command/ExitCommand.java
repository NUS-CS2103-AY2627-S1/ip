package pdd.command;

import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

/** Prints the goodbye message and signals the main loop to stop. */
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
