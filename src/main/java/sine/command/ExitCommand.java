package sine.command;

import sine.storage.Storage;
import sine.task.TaskList;
import sine.ui.Ui;

/**
 * Ends the chatbot session after showing the farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Shows the farewell message without changing the task list.
     *
     * @param tasks current task list
     * @param ui console user interface
     * @param storage task persistence service
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that this command ends the application.
     *
     * @return true
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
