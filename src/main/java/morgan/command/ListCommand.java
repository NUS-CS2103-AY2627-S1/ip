package morgan.command;

import morgan.exception.MorganException;
import morgan.storage.Storage;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Represents a command in the application.
 */
public class ListCommand extends Command {
    /**
     * Executes the list command using the given tasks, ui, and storage.
     *
     * @param tasks The list of tasks.
     * @param ui The ui.
     * @param storage The storage file.
     * @return False.
     */
    @Override
    public boolean execute(TaskList tasks, Ui ui, Storage storage) throws MorganException {
        if (tasks.size() == 0) {
            ui.showToUser(" Meow~ No fish in your pond yet!");
            return false;
        }
        ui.showToUser(" Meow~ Here are the fishes in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showToUser(String.format(" %d.%s", i + 1, tasks.get(i)));
        }
        return false;
    }
}
