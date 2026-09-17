package yachiyo.command;

import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Represents an executable command entered by the user.
 */
public abstract class Command {
    /**
     * Executes the command using the application's task list, UI, and storage.
     *
     * @param tasks task list on which the command operates.
     * @param ui user interface used to display command results.
     * @param storage storage used to persist task changes.
     * @throws YachiyoException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws YachiyoException;

    /**
     * Checks whether executing this command should end the application.
     *
     * @return true if this is an exit command.
     */
    public boolean isExit() {
        return false;
    }
}
