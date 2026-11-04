package soar.command;

import soar.storage.Storage;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Ends the current Soar session after showing the farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current Soar session.
     */
    public ExitCommand() {
    }

    /**
     * Displays the farewell message for the current session.
     *
     * @param tasks unused task-list dependency supplied by the command interface
     * @param ui interface used to display the farewell message
     * @param storage unused storage dependency supplied by the command interface
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
