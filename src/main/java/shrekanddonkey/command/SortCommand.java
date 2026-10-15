package shrekanddonkey.command;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Represents the command that sorts tasks by their relevant date.
 */
public class SortCommand extends Command {
    /**
     * Sorts tasks by deadline or event start date and displays the result.
     *
     * @param tasks application's task list.
     * @param ui application's user interface.
     * @param storage application's file storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "TaskList collaborator cannot be null";
        assert ui != null : "Ui collaborator cannot be null";

        tasks.sortByDate();
        ui.showTaskList(tasks.getTasks());
        ui.showDivider();
    }

    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
