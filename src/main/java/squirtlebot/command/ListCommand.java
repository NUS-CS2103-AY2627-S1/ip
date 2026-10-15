package squirtlebot.command;

import squirtlebot.storage.Storage;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;

/**
 * Represents the list command within {@code SquirtleBot}.
 */
public class ListCommand extends Command {

    /**
     * Displays the tasks within the task list to the user.
     *
     * @param tasks list containing tasks created previously by the user.
     * @param ui interface used to display output to the user.
     * @param storage unused storage handler, included to override implementation in {@link Command}.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.setSavedMessage("Your list of tasks is currently empty :)");
            return;
        }
        ui.setSavedMessage(tasks.toString());
    }
}
