package megatron.command;

import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Displays all tasks in the task list.
 */
public final class ListCommand extends Command {
    /**
     * Creates a command that lists all tasks.
     */
    public ListCommand() { }

    /**
     * Displays the current tasks without modifying them.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) {
        ui.showTasks(tasks);
    }
}
