package echo.command;

import java.util.List;
import java.util.Locale;

import echo.EchoException;
import echo.task.SortCriteria;
import echo.task.Task;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents a command to sort the tasks in the task list.
 */
public class SortCommand extends Command {
    private final SortCriteria criteria;

    /**
     * Creates a sort command with the specified sorting criteria.
     *
     * @param criteria criteria to sort tasks by.
     */
    public SortCommand(SortCriteria criteria) {
        super(Type.SORT, List.of(criteria.name().toLowerCase(Locale.ROOT)));
        this.criteria = criteria;
    }

    /**
     * Sorts the tasks and displays the sorted list.
     *
     * @param taskManager service used to perform task operations.
     * @param ui interface used to display results.
     * @return response message confirming the sort operation.
     * @throws EchoException if sorting or persistence fails.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) throws EchoException {
        List<Task> sortedTasks = taskManager.sortTasks(criteria);
        return ui.showSorted(sortedTasks);
    }
}
