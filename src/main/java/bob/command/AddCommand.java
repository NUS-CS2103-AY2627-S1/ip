package bob.command;

import java.util.Optional;

import bob.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Adds one task to the list.
 *
 * <p>One class serves {@link CommandWord#TODO}, {@link CommandWord#DEADLINE} and
 * {@link CommandWord#EVENT} alike, because what it holds is a {@link Task} and
 * the three differ only in which kind of task that is. {@link bob.parser.Parser Parser} has
 * already decided which kind and built it, so there is nothing left here that
 * varies between them: the task is stored, shown back, and saved the same way
 * whichever it is.
 */
public class AddCommand extends Command {

    /** The task to add, already built from what the user typed. */
    private final Task task;

    /**
     * Creates a command that will add one task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task to the list, shows it back along with how many tasks there now
     * are, and saves the changed list.
     *
     * <p>A task the list already holds is refused instead, so nothing is added or
     * saved. The task already there is named by its number and shown, since capitals
     * and spaces are ignored when matching, and it may not look exactly like what was
     * typed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobException {
        Optional<Integer> duplicateIndex = tasks.findDuplicate(task);
        if (duplicateIndex.isPresent()) {
            int index = duplicateIndex.get();
            // The user counts from 1, the list counts from 0.
            throw new BobException("Yu already have dis as task " + (index + 1) + ": " + tasks.get(index));
        }
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
        storage.save(tasks.asList());
    }
}
