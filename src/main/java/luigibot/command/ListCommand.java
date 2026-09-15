package luigibot.command;

import luigibot.storage.Storage;
import luigibot.task.TaskList;
import luigibot.ui.Ui;

/**
 * Displays all tasks currently stored in LuigiBot.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that displays the current task list.
     */
    public ListCommand() {
    }

    /**
     * Displays the current task list.
     *
     * @param tasks stored tasks.
     * @param ui user interface used to display the tasks.
     * @param storage storage dependency required by the common command interface.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
