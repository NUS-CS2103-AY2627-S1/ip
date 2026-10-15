package nova.command;

import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that displays all tasks.
     */
    public ListCommand() {
    }

    /**
     * Displays every task in the task list.
     *
     * @param tasks task list to display
     * @param ui UI used to display the tasks
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTasks(tasks.getTasks());
    }
}
