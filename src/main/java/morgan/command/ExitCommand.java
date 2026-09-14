package morgan.command;

import morgan.storage.Storage;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Represents a command in the application.
 */
public class ExitCommand extends Command {
    /**
     * Executes the exit command using the given tasks, ui, and storage.
     *
     * @param tasks The list of tasks.
     * @param ui The ui.
     * @param storage The storage file.
     * @return True.
     */
    @Override
    public boolean execute(TaskList tasks, Ui ui, Storage storage) {
        return true;
    }
}
