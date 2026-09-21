package pdd.command;

import pdd.PDDException;
import pdd.storage.Storage;
import pdd.task.Task;
import pdd.task.TaskList;
import pdd.ui.Ui;

/**
 * Shared logic for {@link MarkCommand} and {@link UnmarkCommand}: both
 * resolve the same 1-based task number, apply a done/not-done change to
 * that task, save, and print a confirmation. Subclasses only need to say
 * which status to apply and which confirmation to show.
 */
public abstract class MarkStatusCommand extends Command {
    private final String taskNumberArg;

    /** Creates a command that will act on the task at the given 1-based position (as typed by the user). */
    protected MarkStatusCommand(String taskNumberArg) {
        this.taskNumberArg = taskNumberArg;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PDDException {
        int index = tasks.toIndex(taskNumberArg);
        Task task = tasks.get(index);
        applyStatus(task);
        storage.save(tasks.getTasks());
        showResult(ui, task);
    }

    /** Applies this command's done/not-done change to the given task. */
    protected abstract void applyStatus(Task task);

    /** Prints the confirmation for this command's change to the given task. */
    protected abstract void showResult(Ui ui, Task task);
}
