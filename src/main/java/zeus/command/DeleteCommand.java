package zeus.command;

import zeus.exception.ZeusException;
import zeus.task.Task;
import zeus.task.TaskList;

/** Deletes a numbered task from the task list. */
public class DeleteCommand extends Command {
    /** One-based number of the task to delete. */
    private final int taskNumber;

    /**
     * Creates a command for the selected task number.
     * @param taskNumber one-based task number
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes and saves the task, then displays a confirmation.
     * @param context application components available to the command
     * @throws ZeusException if the task number is invalid or saving fails
     */
    @Override
    public void execute(CommandContext context) throws ZeusException {
        TaskList tasks = context.getTasks();
        Task removedTask = tasks.delete(taskNumber);
        context.getStorage().save(tasks);
        context.getUi().showTaskDeleted(removedTask, tasks.size());
    }
}
