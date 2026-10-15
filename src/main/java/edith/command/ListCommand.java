package edith.command;

import edith.task.TaskList;
import edith.ui.Ui;

/** Displays all tasks currently stored by Edith. */
public class ListCommand extends Command {
    /**
     * Displays all tasks without modifying the task list.
     *
     * @param tasks the task list to display
     * @param ui the user interface used to display the tasks
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTaskList(tasks);
    }
}
