package lizzy.command;

import lizzy.exception.LizzyException;
import lizzy.storage.Storage;
import lizzy.task.Task;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Marks one numbered task as incomplete.
 */
public class UnmarkCommand extends Command {
    /**
     * The one-based task number to mark as incomplete.
     */
    private final int taskNumber;

    /**
     * Creates a command that unmarks the supplied task number.
     *
     * @param taskNumber the one-based number of the task to mark as incomplete
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks, saves, and confirms the referenced task.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LizzyException {
        Task task = tasks.getTask(taskNumber, "unmark");
        if (!task.isDone()) {
            throw new LizzyException("That matter is already waiting to be done.\n"
                    + "There is nothing to reopen just yet.");
        }
        task.markAsNotDone();
        storage.save(tasks.asList());
        ui.showTaskUnmarked(task);
    }
}
