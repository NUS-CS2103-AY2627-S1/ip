package greenchonk.command;

import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Ends the current Green Chonk session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current session.
     */
    public ExitCommand() {
    }

    /**
     * Displays Green Chonk's farewell without changing the task list.
     *
     * @param tasks the unchanged task list.
     * @param ui the UI that displays the farewell.
     * @param storage the unused task storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
