package windy.command;

import windy.task.Task;
import windy.task.TaskList;

/**
 * Adds a task to the task list.
 */
public class AddTaskCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task the task to add.
     */
    public AddTaskCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(CommandContext context) {
        TaskList tasks = context.getTasks();
        if (!context.applyTaskChange(candidate -> candidate.addTask(task.copy()))) {
            return;
        }
        context.getUi().showAddTask(task, tasks.getNumTasks());
    }
}
