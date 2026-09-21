package pdd.command;

import pdd.PDDException;
import pdd.storage.Storage;
import pdd.task.Task;
import pdd.task.TaskList;
import pdd.ui.Ui;

/** Removes the task at the given 1-based position from the list. */
public class DeleteCommand extends Command {
    private final String taskNumberArg;

    /** Creates a command that will remove the task at the given 1-based position (as typed by the user). */
    public DeleteCommand(String taskNumberArg) {
        this.taskNumberArg = taskNumberArg;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PDDException {
        int index = tasks.toIndex(taskNumberArg);
        Task removed = tasks.delete(index);
        storage.save(tasks.getTasks());
        ui.showDeleted(removed, tasks.size());
    }
}
