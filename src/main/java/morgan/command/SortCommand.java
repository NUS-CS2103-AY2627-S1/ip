package morgan.command;

import morgan.exception.MorganException;
import morgan.storage.Storage;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Sorts all tasks alphabetically by their descriptions.
 */
public class SortCommand extends Command {
    /**
     * Sorts the task list, saves the new order, and displays the sorted tasks.
     *
     * @param tasks The list of tasks.
     * @param ui The user interface.
     * @param storage The storage file.
     * @return False because this command does not exit the application.
     * @throws MorganException If a task cannot be retrieved for display.
     */
    @Override
    public boolean execute(TaskList tasks, Ui ui, Storage storage) throws MorganException {
        if (tasks.size() == 0) {
            ui.showToUser(" Meow~ No fish to sort in your pond!");
            return false;
        }

        tasks.sortByName();
        storage.save(tasks.getTasks());
        ui.showToUser(" Meow~ I sorted your fishes alphabetically:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showToUser(String.format(" %d.%s", i + 1, tasks.get(i)));
        }
        return false;
    }
}
