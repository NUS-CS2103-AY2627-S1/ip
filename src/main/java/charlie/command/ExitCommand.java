package charlie.command;

import charlie.storage.Storage;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Ends the current Charlie session.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /**
     * Displays Charlie's farewell message.
     *
     * @param tasks Task collection, which this command does not use.
     * @param ui User interface used to display the farewell.
     * @param storage Storage collaborator, which this command does not use.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showOutro();
    }

    /**
     * Signals that Charlie should end the command loop.
     *
     * @return Always true for an exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
