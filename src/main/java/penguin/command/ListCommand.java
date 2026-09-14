package penguin.command;

import penguin.storage.Storage;
import penguin.task.TaskList;
import penguin.ui.Ui;

/**
 * Represents a command that lists all tasks.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     */
    public ListCommand() {
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks task list to display.
     * @param ui interface used for output.
     * @param storage unused storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "List command requires a task list";
        assert ui != null : "List command requires a user interface";

        ui.showTasks(tasks);
    }
}
