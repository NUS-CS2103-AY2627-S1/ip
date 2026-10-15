package zeus.command;

import zeus.exception.ZeusException;
import zeus.task.Task;
import zeus.task.TaskList;

/** Marks a numbered task as incomplete. */
public class UnmarkCommand extends Command {
    /** One-based number of the task to unmark. */
    private final int taskNumber;

    /**
     * Creates a command for the selected task number.
     * @param taskNumber one-based task number
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks and saves the task, then displays a confirmation.
     * @param context application components available to the command
     * @throws ZeusException if the task number is invalid or saving fails
     */
    @Override
    public void execute(CommandContext context) throws ZeusException {
        TaskList tasks = context.getTasks();
        Task task = tasks.unmark(taskNumber);
        context.getStorage().save(tasks);
        context.getUi().showTaskUnmarked(task);
    }
}
