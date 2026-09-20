package fifi.command;

import fifi.TaskList;
import fifi.exception.FifiException;
import fifi.task.Task;

/**
 * Adds one new task to the task list.
 */
public abstract class AddTaskCommand extends TaskChangingCommand {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add
     */
    public AddTaskCommand(Task task) {
        this.task = task;
    }

    @Override
    protected String[] applyChange(TaskList tasks) throws FifiException {
        tasks.add(task);
        return new String[]{
            "Got it. I've added this task:",
            task.toString(),
            String.format("Now you have %d tasks in the list.", tasks.size())};
    }
}
