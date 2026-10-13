package arrodes.command;

import arrodes.storage.Storage;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Command that displays the farewell message and exits the application. */
public class ByeCommand extends Command {
    /** Creates an exit command. */
    public ByeCommand() {
    }

    /** Always requests application termination. */
    @Override
    public boolean isExit() {
        return true;
    }

    /**
     * Displays the farewell message.
     * @param ui interface used for output
     * @param taskList unused task list
     * @param storage unused storage
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        ui.showOnExitMessage();
    }
}
