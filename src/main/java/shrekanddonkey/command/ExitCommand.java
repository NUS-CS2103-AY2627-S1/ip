package shrekanddonkey.command;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Represents the command that ends the chatbot session.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /**
     * Displays the chatbot's goodbye message.
     *
     * @param tasks application's task list, which is not used.
     * @param ui application's user interface.
     * @param storage application's file storage, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "TaskList collaborator cannot be null";
        assert ui != null : "Ui collaborator cannot be null";
        assert storage != null : "Storage collaborator cannot be null";
        ui.showGoodbye();
    }

    /**
     * Indicates that this command ends the chatbot session.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}

