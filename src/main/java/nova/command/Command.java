package nova.command;

import java.io.IOException;

import nova.exception.NovaException;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Represents a user command that can be executed by Nova.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    public Command() {
    }

    /**
     * Executes this command.
     *
     * @param tasks task list to inspect or update.
     * @param ui UI used to display the result.
     * @throws NovaException if the command cannot be applied to the task list.
     * @throws IOException if a task-list change cannot be saved.
     */
    public abstract void execute(TaskList tasks, Ui ui) throws NovaException, IOException;

    /**
     * Returns whether this command exits Nova.
     *
     * @return {@code true} if this command exits Nova
     */
    public boolean isExit() {
        return false;
    }
}
