package command;

import exception.CbtException;
import task.TaskList;

/** Displays every task in the list. */
public class ListCommand implements Command {
    /**
     * Returns every task in its current list order.
     *
     * @param taskList task list to display.
     * @return result indicating that no application state changed.
     * @throws CbtException if a task cannot be retrieved for display.
     */
    @Override
    public CommandResult execute(TaskList taskList) throws CbtException {
        String message = "Your current flight plan:";
        String formattedTasks = taskList.formatTasks();
        if (!formattedTasks.isEmpty()) {
            message += System.lineSeparator() + formattedTasks;
        }
        return new CommandResult(message, false, false);
    }
}
