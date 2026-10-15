package bob.command;

import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Prints every task, numbered from 1.
 *
 * <p>This changes nothing, so nothing is saved afterwards and the
 * {@link Storage} it is handed goes unused.
 */
public class ListCommand extends Command {

    /**
     * Prints every task in the list, numbered from 1, or says that nothing has been
     * added yet.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks, tasks.getAllIndexes(),
                "Luk at tu! Here da tasks:",
                "No tasks yet! Bob bored...");
    }
}
