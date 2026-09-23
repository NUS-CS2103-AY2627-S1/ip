package sallman.command;

import sallman.Parser;
import sallman.SallmanException;
import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;

/**
 * Puts the task list into a chosen order.
 * <p>
 * The new order is kept: it is written to the data file like any other change,
 * rather than being a one-off view of the list.
 */
public class SortCommand extends Command {

    /** The order the user asked for, checked when the command runs. */
    private final String arguments;

    /**
     * Creates a command that sorts the list into the given order.
     *
     * @param arguments text the user typed after the command
     */
    public SortCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Sorts the list, shows it in its new order, and saves it.
     * <p>
     * A list already in the order asked for is shown as it is, without saving it
     * or leaving an undo step, as marking and tagging do when nothing would change.
     *
     * @param tasks   the list to sort
     * @param ui      used to show the sorted list
     * @param storage used to save the reordered list
     * @throws SallmanException if the order is not one this understands, or if
     *                          the list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SallmanException {
        SortOrder order = Parser.parseSortOrder(arguments);
        boolean isChanging = !tasks.isSortedBy(order.getComparator());
        if (isChanging) {
            tasks.saveSnapshot();
            tasks.sort(order.getComparator());
        }
        ui.showSorted(tasks, order.getKeyword());
        if (isChanging) {
            storage.save(tasks.asList());
        }
    }
}
