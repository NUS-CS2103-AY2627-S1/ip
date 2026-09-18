package zhangwei.command;

import zhangwei.ZhangWeiException;
import zhangwei.storage.Storage;
import zhangwei.task.SortCriterion;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/**
 * Puts the task list into a given order and keeps it that way.
 *
 * <p>The new order is saved rather than shown once, so the numbers the user
 * reads off the sorted list are the numbers the next mark or delete will use.
 */
public class SortCommand extends Command {

    private final SortCriterion criterion;

    /**
     * Creates a command that will sort the list by the given criterion.
     *
     * @param criterion the order to put the tasks into.
     */
    public SortCommand(SortCriterion criterion) {
        this.criterion = criterion;
    }

    /**
     * Sorts the list, shows it in its new order, and saves it.
     *
     * <p>An empty list is said to be empty instead of being sorted and shown,
     * because a heading with nothing under it reads as though something went
     * wrong.
     *
     * @param tasks the task list to sort.
     * @param ui used to confirm the new order and show it.
     * @param storage used to save the reordered list.
     * @throws ZhangWeiException if the reordered list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZhangWeiException {
        if (tasks.isEmpty()) {
            ui.showMessage("You have no tasks to sort.");
            return;
        }

        tasks.sort(criterion);
        ui.showTasksSorted(criterion, tasks);
        storage.saveTasks(tasks);
    }
}
