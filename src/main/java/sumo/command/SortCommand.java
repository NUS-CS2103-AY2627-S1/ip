package sumo.command;

import java.util.List;

import sumo.storage.Storage;
import sumo.task.Task;
import sumo.task.TaskList;
import sumo.ui.Ui;

/** Displays a temporary chronological view of every task. */
public class SortCommand extends Command {
    /** Creates a command that displays tasks in chronological order. */
    public SortCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> sortedTasks = tasks.getChronologicallySortedTasks();
        if (sortedTasks.isEmpty()) {
            ui.showNoTasksToSort();
        } else {
            ui.showSortedTaskList(sortedTasks);
        }
    }
}
