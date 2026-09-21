package goat.command;

import goat.GoatException;
import goat.storage.Storage;
import goat.task.SortOrder;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * Puts the task list into a chosen order.
 *
 * Unlike {@link ListCommand}, which only shows the tasks, this one changes
 * their order and saves it, so the list stays sorted after a restart. That also
 * means the numbers the user types change: after sorting, "1" is whatever now
 * sits at the top, in the same way it is after a delete.
 */
public class SortCommand extends Command {

    /** The order to put the tasks into. */
    private final SortOrder order;

    /**
     * Creates a command that will sort by the given order.
     *
     * @param order how the tasks should be arranged
     */
    public SortCommand(SortOrder order) {
        this.order = order;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reorders the list, writes it to disk, and shows the result numbered,
     * so the user can see the new numbers straight away rather than having to
     * ask for the list again.
     *
     * @throws GoatException if the reordered list cannot be written to disk
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws GoatException {
        if (tasks.isEmpty()) {
            ui.show("Nothing to sort.");
            return;
        }
        tasks.sort(order);
        storage.save(tasks);
        ui.show(formatNumbered("Sorted by " + order.getKeyword() + ".", tasks));
    }
}
