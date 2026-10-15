package billy.command;

import java.util.List;

import billy.storage.Storage;
import billy.task.Task;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Adds a task to the list.
 *
 * <p>One class covers todos, deadlines and events alike, because adding is the
 * same act whatever is being added. Which kind of task it is was settled by
 * {@link billy.parser.Parser Parser}, and the task itself knows how to describe
 * and store itself, so there is nothing here that differs between the three.
 */
public class AddCommand extends Command {

    /** The task to add, already built and known to be valid. */
    private final Task task;

    /**
     * Creates a command that will add one task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task to the list, says so, and writes the list out.
     *
     * <p>The confirmation names the task and how many there now are, so the user
     * can see both what was added and where that leaves them.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Looked for before the task is added, so the new one does not count as
        // a match of itself.
        List<Integer> alreadyThere = tasks.findSameTasks(task);
        tasks.add(task);

        if (alreadyThere.isEmpty()) {
            ui.show("Consider it written down:",
                    "  " + task,
                    Ui.describeNewListSize(tasks.size()));
        } else {
            // The task is still added: two tasks worded alike can be two real
            // errands, and refusing the second would be Billy deciding that for
            // the user. Saying which number to delete makes undoing it one
            // command, which is cheaper than being asked to confirm every time.
            ui.show("Consider it written down:",
                    "  " + task,
                    Ui.describeNewListSize(tasks.size()),
                    "Heads up: that's the same as " + Ui.describeTaskNumbers(alreadyThere)
                            + ". Type 'delete " + tasks.size() + "' if you didn't mean it.");
        }
        save(tasks, ui, storage);
    }
}
