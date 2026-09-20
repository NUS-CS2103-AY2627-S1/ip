package edith.command;

import java.util.List;

import edith.EdithException;
import edith.Storage;
import edith.Ui;
import edith.task.SortOrder;
import edith.task.Task;
import edith.task.TaskList;

/** Sorts and saves Edith's task list. */
public class SortCommand extends Command {
    private final SortOrder sortOrder;

    /**
     * Creates a command that arranges tasks in the given order.
     *
     * @param sortOrder order in which to arrange the tasks
     */
    public SortCommand(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EdithException {
        List<Task> previousOrder = tasks.getTasksInCurrentOrder();
        tasks.sort(sortOrder);
        try {
            storage.save(tasks);
        } catch (EdithException error) {
            tasks.restoreOrder(previousOrder);
            throw error;
        }
        ui.showList(tasks);
    }
}
