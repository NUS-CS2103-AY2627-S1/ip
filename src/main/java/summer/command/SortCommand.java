package summer.command;

import summer.storage.Storage;
import summer.task.TaskList;
import summer.ui.Ui;

/** The "sort" command: orders the task list by date and saves the new order. */
public class SortCommand extends Command {
    /** Sorts the list by date, shows the sorted list, and persists the new order. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String sortedList = tasks.sort();
        storage.save(tasks);
        ui.showMessage(sortedList);
    }
}
