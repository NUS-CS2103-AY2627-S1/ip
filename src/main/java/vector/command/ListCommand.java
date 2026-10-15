package vector.command;

import vector.storage.Storage;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to list all tasks in the task list.
 */
public class ListCommand extends Command {
    /**
     * Executes the command, displaying all tasks currently in the list.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface for displaying messages.
     * @param storage The storage for saving updates.
     * @return The response string.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.size() == 0) {
            return "Your task list is empty.";
        }
        String taskListStr = java.util.stream.IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i).toString())
                .collect(java.util.stream.Collectors.joining("\n"));
        return "Accessing database... Current task matrix:\n" + taskListStr;
    }
}
