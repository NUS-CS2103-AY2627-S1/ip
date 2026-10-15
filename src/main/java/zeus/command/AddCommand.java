package zeus.command;

import zeus.exception.ZeusException;
import zeus.task.Task;
import zeus.task.TaskList;

/** Adds a parsed task to the task list. */
public class AddCommand extends Command {
    /** Task to add when this command executes. */
    private final Task task;

    /**
     * Creates a command for the specified task.
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds and saves the task, then displays a confirmation.
     * @param context application components available to the command
     * @throws ZeusException if the task list cannot be saved
     */
    @Override
    public void execute(CommandContext context) throws ZeusException {
        TaskList tasks = context.getTasks();
        tasks.add(task);
        context.getStorage().save(tasks);
        context.getUi().showTaskAdded(task, tasks.size());
    }
}
