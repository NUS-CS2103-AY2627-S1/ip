package dennis.command;

import dennis.DennisException;
import dennis.storage.Storage;
import dennis.task.Task;
import dennis.task.TaskList;
import dennis.ui.Ui;

/**
 * Adds a ready-made task (todo, deadline or event) to the list and saves
 * the change.
 *
 * <p>The task itself is built by {@link dennis.parser.Parser} while the
 * command line is being parsed, so any problem with its contents (empty
 * description, a {@code '|'} character, an unparseable date) is reported
 * before this command object is even created. By the time {@code execute}
 * runs the task is known to be valid; the one thing still checked here is
 * that it is not an exact duplicate of a task already in the list, which
 * {@link TaskList#add} enforces.</p>
 */
public class AddCommand extends Command {
    /** The already-validated task to add. */
    private final Task task;

    /**
     * Wraps an already-validated task so it can be added when this command runs.
     *
     * @param task the task to add to the list
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws DennisException {
        tasks.add(task);
        storage.save(tasks.asList());
        ui.showAddedTask(task, tasks.size());
    }
}
