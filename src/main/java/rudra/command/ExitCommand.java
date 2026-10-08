package rudra.command;

import java.util.ArrayList;

import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Ends the chatbot session.
 */
public class ExitCommand extends Command {
    /**
     * Shows the farewell message for the current session.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper kept for a consistent command interface.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that this command ends the application loop.
     *
     * @return Always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
