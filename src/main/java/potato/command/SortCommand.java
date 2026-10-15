package potato.command;

import potato.exception.PotatoException;
import potato.storage.Storage;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Handles sorting tasks in alphabetical order.
 */
public class SortCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PotatoException {
        if (tasks.size() == 0) {
            throw new PotatoException("The task list is empty! Nothing to sort.");
        }

        tasks.sortTasks((t1, t2) -> t1.getDescription().compareToIgnoreCase(t2.getDescription()));
        storage.save(tasks.getTasks());
        ui.showMessage("Sorted tasks alphabetically by description!");
        ui.showTaskList(tasks);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}