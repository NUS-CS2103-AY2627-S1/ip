package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Puts the task list in order, earliest first.
 *
 * <p>The new order is kept rather than being shown once and forgotten: it is
 * saved, so the list stays sorted in later sessions, and the numbers the user
 * types afterwards refer to the order they are now looking at.
 */
public class SortCommand extends Command {
    /**
     * Creates a command that will sort the task list.
     */
    public SortCommand() {
    }

    /**
     * Sorts the list, shows the result, and saves the new order.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.showMessage("Your task list is empty, so there is nothing to sort.");
            return;
        }

        tasks.sortBySchedule();

        ui.showMessage("Sorted your tasks, earliest first:");
        showAllTasks(tasks, ui);
        saveTasks(tasks, ui, storage);
    }
}
