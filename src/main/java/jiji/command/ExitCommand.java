package jiji.command;

import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Command that terminates the Jiji chatbot session.
 */
public class ExitCommand extends Command {

    /**
     * Constructs a new ExitCommand.
     */
    public ExitCommand() {
    }

    /**
     * Executes the exit command by displaying the farewell message.
     *
     * @param tasks The task list.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
        return ui.formatGoodbye();
    }

    /**
     * Returns true to signal application termination.
     *
     * @return True.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
