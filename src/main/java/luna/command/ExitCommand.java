package luna.command;

import luna.storage.Storage;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents the command that exits the application.
 */
public class ExitCommand extends Command {
    /**
     * Shows the farewell message before the application terminates.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showExit();
    }

    /**
     * Returns whether this command should terminate the application.
     *
     * @return Always {@code true} for the exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
