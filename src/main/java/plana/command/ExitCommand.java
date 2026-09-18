package plana.command;

import plana.storage.Storage;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Exits Plana after displaying its goodbye message.
 */
public class ExitCommand extends Command {
    /**
     * Displays the goodbye response.
     *
     * @param tasks unused by this command.
     * @param ui the user interface used for the goodbye response.
     * @param storage unused by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Identifies this command as the command that ends the application.
     *
     * @return always true for an exit command
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
