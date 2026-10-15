package bob.command;

import bob.Storage;
import bob.Ui;
import bob.task.TaskList;

/**
 * Represents a request to exit Bob.
 */
public class ExitCommand extends Command {
    /**
     * Displays Bob's farewell without changing the task list.
     *
     * @param tasks task list, which is not changed
     * @param ui user interface used to display the farewell
     * @param storage storage, which is not accessed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that this command ends the application.
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
