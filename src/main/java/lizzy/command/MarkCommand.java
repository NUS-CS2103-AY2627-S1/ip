package lizzy.command;

import lizzy.exception.LizzyException;
import lizzy.storage.Storage;
import lizzy.task.Task;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Marks one numbered task as complete.
 */
public class MarkCommand extends Command {
    /**
     * The one-based task number to mark.
     */
    private final int taskNumber;

    /**
     * Creates a command that marks the supplied task number.
     *
     * @param taskNumber the one-based number of the task to mark
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks, saves, and confirms the referenced task.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LizzyException {
        Task task = tasks.getTask(taskNumber, "mark");
        if (task.isDone()) {
            throw new LizzyException("That matter is already settled.\n"
                    + "Even diligence need not do the same work twice.");
        }
        task.markAsDone();
        storage.save(tasks.asList());
        ui.showTaskMarked(task);
    }
}
