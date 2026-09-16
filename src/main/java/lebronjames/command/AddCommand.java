package lebronjames.command;

import lebronjames.LebronJamesException;
import lebronjames.storage.Storage;
import lebronjames.task.Task;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Adds a task to the list.
 *
 * <p>One class covers to-dos, deadlines, and events: by the time the command
 * exists, {@link lebronjames.parser.Parser Parser} has already built the right kind of {@link Task}, and
 * adding it is the same work in all three cases. Splitting this into
 * AddTodoCommand, AddDeadlineCommand, and AddEventCommand would give three
 * classes with identical bodies.
 *
 * <p>This is also where a task the user already has is turned away. The check
 * lives here rather than in {@link TaskList} because refusing to add is a
 * decision about this command: the task list still answers whether a duplicate
 * exists, but adding is the only situation in which that answer is a problem.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LebronJamesException {
        int duplicateNumber = tasks.findDuplicateNumber(task);
        if (duplicateNumber != TaskList.NO_DUPLICATE) {
            throw new LebronJamesException("TRAVELLING! That play is already on the board, as number "
                    + duplicateNumber + ":\n  " + task
                    + "\nIf you really need it twice, give one of them a different description.");
        }

        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.asList());
    }
}
