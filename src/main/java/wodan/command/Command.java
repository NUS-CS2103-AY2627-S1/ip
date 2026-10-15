package wodan.command;

import wodan.WodanException;
import wodan.storage.Storage;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * A user command that can be carried out against the task list.
 */
public abstract class Command {
    /**
     * Creates a command to be executed against the task list.
     */
    protected Command() {
    }

    /**
     * Carries out this command.
     *
     * @param tasks The task list to read or change.
     * @param ui The user interface for replies.
     * @param storage The save file, used when this command changes tasks.
     * @throws WodanException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException;

    /**
     * Returns {@code true} if the chatbot should stop after this command.
     *
     * @return Whether this is an exit command. The default is {@code false}.
     */
    public boolean isExit() {
        return false;
    }
}
