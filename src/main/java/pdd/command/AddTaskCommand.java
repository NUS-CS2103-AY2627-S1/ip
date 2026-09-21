package pdd.command;

import pdd.storage.Storage;
import pdd.task.Task;
import pdd.task.TaskList;
import pdd.ui.Ui;

/**
 * Shared logic for {@link TodoCommand}, {@link DeadlineCommand} and
 * {@link EventCommand}: all three add an already-parsed task, save, and
 * show the same confirmation, differing only in which concrete task type
 * they carry.
 */
public abstract class AddTaskCommand extends Command {
    private final Task task;

    /** Creates a command that will add the given already-parsed task to the task list. */
    protected AddTaskCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showAdded(task, tasks.size());
    }
}
