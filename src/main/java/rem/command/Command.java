package rem.command;

import java.io.IOException;

import rem.exception.RemException;
import rem.storage.Storage;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Represents an instruction that can be executed by Rem.
 */
public abstract class Command {
    /**
     * Executes this command.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display command results.
     * @param storage Storage used to save task-list changes.
     * @throws IOException If the command cannot save a task-list change.
     * @throws RemException If the command cannot be applied to the current task list.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws IOException, RemException;

    /**
     * Returns whether this command should end the application.
     *
     * @return True if Rem should stop after executing this command.
     */
    public boolean isExit() {
        return false;
    }
}
