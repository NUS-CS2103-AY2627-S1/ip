package fifi.command;

import fifi.TaskList;
import fifi.task.Task;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends TaskChangingCommand {
    private final int taskIndex;

    /**
     * Creates a command that deletes the task at the given zero-based index.
     *
     * @param taskIndex the zero-based task index
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    protected String[] applyChange(TaskList tasks) {
        Task removedTask = tasks.delete(taskIndex);
        return new String[]{
            "Got it. I've removed this task:",
            "    " + removedTask,
            String.format("Now you have %d tasks in the list ^^.", tasks.size())};
    }
}
