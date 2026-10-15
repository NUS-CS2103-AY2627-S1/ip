package sine.command;

import java.io.IOException;

import sine.exception.SineException;
import sine.storage.Storage;
import sine.task.TaskList;
import sine.ui.Ui;

/**
 * Represents an instruction that can be executed by the chatbot.
 */
public abstract class Command {
    /**
     * Executes this command using the application's shared components.
     *
     * @param tasks Current task list.
     * @param ui Console user interface.
     * @param storage Task persistence service.
     * @throws SineException If the command cannot be performed in the current state.
     * @throws IOException If the command cannot save task changes.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SineException;

    /**
     * Indicates whether executing this command should end the application.
     *
     * @return True only for an exit command.
     */
    public boolean isExit() {
        return false;
    }
}
