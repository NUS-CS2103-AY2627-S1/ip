package bob.command;

import java.util.List;

import bob.ResponseType;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Command that displays all stored tasks.
 */
public class ListCommand extends Command {
    /** Creates a list command. */
    public ListCommand() {
    }

    /**
     * Displays every task in the current task list.
     *
     * @param tasks task list to display.
     * @param storage task storage, which is unchanged.
     * @return response containing the numbered tasks.
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        List<Task> currentTasks = tasks.getTasks();
        return formatTasks("Here's your mission list:", currentTasks);
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.LIST;
    }
}
