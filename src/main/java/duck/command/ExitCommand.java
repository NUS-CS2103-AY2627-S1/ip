package duck.command;

import duck.storage.Storage;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Ends the current Duck session after displaying the farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /** Displays Duck's farewell response. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Indicates that the command loop should stop after this command. */
    @Override
    public boolean isExit() {
        return true;
    }
}
