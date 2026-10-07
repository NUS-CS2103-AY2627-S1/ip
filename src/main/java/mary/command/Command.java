package mary.command;

import mary.storage.Storage;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Base type for executable chatbot commands.
 */
public abstract class Command {
    /**
     * Executes the command, displaying any handled validation or persistence errors.
     *
     * @param tasks current in-memory task list.
     * @param ui user-facing message handler.
     * @param storage persistence service for commands that modify tasks.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Indicates whether the command loop should stop after execution.
     *
     * @return false unless overridden by an exit command.
     */
    public boolean isExit() {
        return false;
    }
}
