package hse.command;

import java.io.IOException;

import hse.storage.Storage;
import hse.task.TaskList;
import hse.ui.Ui;

/** Sorts dated tasks chronologically and persists the resulting task order. */
public class SortCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.sortByDate();
        storage.save(tasks);
        ui.showTasksSorted();
    }
}
