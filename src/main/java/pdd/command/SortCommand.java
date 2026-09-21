package pdd.command;

import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

/** Sorts the task list (dated tasks chronologically, then undated tasks alphabetically), then shows it. */
public class SortCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.sortTasks();
        storage.save(tasks.getTasks());
        ui.showSorted(tasks.getTasks());
    }
}
