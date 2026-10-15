package shrekanddonkey.command;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Represents the command that displays all tasks.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     */
    public ListCommand() {
    }

    /**
     * Displays all tasks currently stored in the task list.
     *
     * @param tasks application's task list.
     * @param ui application's user interface.
     * @param storage application's file storage, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "TaskList collaborator cannot be null";
        assert ui != null : "Ui collaborator cannot be null";
        assert storage != null : "Storage collaborator cannot be null";
        ui.showTaskList(tasks.getTasks());
        ui.showDivider();
    }
}

