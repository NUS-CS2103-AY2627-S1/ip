package crystal.command;

import crystal.storage.Storage;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Ends the chatbot session after displaying Crystal's farewell.
 */
public final class ExitCommand extends Command {
    /**
     * Creates a command that ends the current chatbot session.
     */
    public ExitCommand() {
    }

    /**
     * Displays the farewell without changing tasks or storage.
     *
     * @param tasks task list; not used by this command.
     * @param ui console interface used to present the farewell.
     * @param storage persistence service; not used by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Identifies this command as the session-ending command.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
