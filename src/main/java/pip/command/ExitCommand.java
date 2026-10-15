package pip.command;

import pip.storage.Storage;
import pip.task.TaskList;
import pip.ui.Ui;


/**
 * Represents a command that ends the program.
 */
public class ExitCommand extends Command {
    /** Creates a command that displays the farewell and signals the console loop to exit. */
    public ExitCommand() {
    }

    /**
     * Shows the farewell message. Does not modify the task list or storage.
     *
     * @param tasks The current task list (unused).
     * @param ui The UI used to show the farewell message.
     * @param storage The current storage (unused).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Signals that the program's main loop should terminate.
     *
     * @return {@code true}, always.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
