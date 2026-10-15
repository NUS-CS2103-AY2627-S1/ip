package chione.command;

import chione.ChioneException;
import chione.Storage;
import chione.Ui;
import chione.task.Task;
import chione.task.TaskList;

/**
 * Adds a task to the list.
 *
 * <p>One class covers todos, deadlines and events alike, because by this point
 * {@link chione.Parser Parser} has already built the task: what differs between the three is
 * how they are read from the input, not what adding one involves.
 */
public class AddCommand extends Command {
    /** The task to add, already built. */
    private final Task task;

    /**
     * Remembers the task to be added.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChioneException {
        // Refused here rather than inside TaskList.add: a repeat is a rule about
        // what the user may type, not about what the list may hold. A loaded save
        // file is taken as written, and it never passes through add anyway.
        if (tasks.hasDuplicateOf(task)) {
            throw new ChioneException("That task is already resting in your list: " + task
                    + ". I haven't added it twice; type list to see it.");
        }
        tasks.add(task);
        ui.showAdded(task, tasks.size());
        storage.save(tasks);
    }
}
