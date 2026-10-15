package clover.command;

import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Displays all tasks in Clover's current task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the task list.
     */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
