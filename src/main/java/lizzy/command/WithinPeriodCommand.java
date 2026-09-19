package lizzy.command;

import lizzy.exception.LizzyException;
import lizzy.storage.Storage;
import lizzy.task.TaskList;
import lizzy.task.WithinPeriodTask;
import lizzy.ui.Ui;

/**
 * Adds one parsed task that can be completed during a date period.
 */
public class WithinPeriodCommand extends Command {
    /**
     * The task to add.
     */
    private final WithinPeriodTask task;

    /**
     * Creates a command that adds the supplied period task.
     *
     * @param task the period task to add
     */
    public WithinPeriodCommand(WithinPeriodTask task) {
        this.task = task;
    }

    /**
     * Adds, saves, and confirms the period task.
     *
     * @param tasks the current task list
     * @param ui the interface for user-facing messages
     * @param storage the task storage service
     * @throws LizzyException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LizzyException {
        tasks.add(task);
        storage.save(tasks.asList());
        ui.showPeriodTaskAdded(task, tasks.size());
    }
}
