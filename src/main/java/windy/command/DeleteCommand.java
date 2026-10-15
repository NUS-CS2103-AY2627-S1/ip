package windy.command;

import windy.task.Task;
import windy.task.TaskList;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that deletes the task at the specified index.
     *
     * @param taskIndex the zero-based index of the task to delete.
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(CommandContext context) {
        TaskList tasks = context.getTasks();
        assert taskIndex >= 0 && taskIndex < tasks.getNumTasks()
                : "Task index should have been validated by Parser";
        Task deletedTask = tasks.getTask(taskIndex);
        if (context.applyTaskChange(candidate -> candidate.deleteTask(taskIndex))) {
            context.getUi().showDeleteTask(deletedTask, tasks.getNumTasks());
        }
    }
}
