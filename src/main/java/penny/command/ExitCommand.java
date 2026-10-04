package penny.command;

import penny.storage.Storage;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Represents a command to terminate and exit the Penny application.
 */
public class ExitCommand extends Command {

    /**
     * Executes the exit command by displaying the goodbye message via the user interface.
     *
     * @param tasks The task list (not modified by this command).
     * @param ui The user interface used to show the exit greeting.
     * @param storage The storage handler (not modified by this command).
     * @return The goodbye message string from the user interface.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "TaskList must not be null";
        assert ui != null : "Ui must not be null";
        assert storage != null : "Storage must not be null";

        return ui.showGoodbye();
    }

    /**
     * Indicates that this command terminates the chatbot execution loop.
     *
     * @return True always, signaling application exit.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
