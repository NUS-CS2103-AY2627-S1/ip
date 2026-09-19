package lizzy.command;

import lizzy.exception.LizzyException;
import lizzy.storage.Storage;
import lizzy.task.Deadline;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Adds one parsed deadline to the task list.
 */
public class DeadlineCommand extends Command {
    /**
     * The deadline to add.
     */
    private final Deadline deadline;

    /**
     * Creates a command that adds the supplied deadline.
     *
     * @param deadline the deadline to add
     */
    public DeadlineCommand(Deadline deadline) {
        this.deadline = deadline;
    }

    /**
     * Adds, saves, and confirms the deadline.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LizzyException {
        tasks.add(deadline);
        storage.save(tasks.asList());
        ui.showDeadlineAdded(deadline, tasks.size());
    }
}
