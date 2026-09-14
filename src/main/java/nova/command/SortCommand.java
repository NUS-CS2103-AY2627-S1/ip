package nova.command;

import nova.NovaException;
import nova.Storage;
import nova.TaskList;
import nova.Ui;

/** Reorders the task list chronologically. */
public class SortCommand extends Command {

    /** Creates a command that sorts the list. */
    public SortCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        tasks.sort();
        storage.save(tasks.asList());
        ui.showSorted(tasks.asList());
    }
}
