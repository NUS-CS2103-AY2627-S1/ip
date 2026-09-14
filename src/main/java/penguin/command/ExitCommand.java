package penguin.command;

import penguin.storage.Storage;
import penguin.task.TaskList;
import penguin.ui.Ui;

/**
 * Represents a command that exits Penguin.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /**
     * Displays the farewell message.
     *
     * @param tasks unused task list.
     * @param ui interface used for output.
     * @param storage unused storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert ui != null : "Exit command requires a ui";

        ui.showGoodbye();
    }

    /**
     * Indicates that this command terminates the application.
     *
     * @return always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
