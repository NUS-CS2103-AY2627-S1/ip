package murphy.command;

import murphy.storage.Storage;
import murphy.task.TaskList;
import murphy.ui.Ui;

/** A command that ends Murphy's conversation. */
public class ExitCommand extends Command {
    /** Displays the farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Bye! I'll be here guarding your task list. Tiny paws, big responsibility.");
    }

    /** Indicates that Murphy should stop after this command. */
    @Override
    public boolean isExit() {
        return true;
    }
}
