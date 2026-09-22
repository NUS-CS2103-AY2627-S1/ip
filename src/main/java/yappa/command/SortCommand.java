package yappa.command;

import yappa.task.SortField;
import yappa.task.SortOrder;
import yappa.task.TaskList;
import yappa.ui.Ui;

/** Sorts tasks by a selected field and order. */
public class SortCommand extends Command {
    private final SortField field;
    private final SortOrder order;

    /**
     * Creates a sorting command.
     *
     * @param field Field by which to sort.
     * @param order Sort direction.
     */
    public SortCommand(SortField field, SortOrder order) {
        this.field = field;
        this.order = order;
    }

    /**
     * Sorts the task list and formats the result.
     *
     * @param tasks Task list to sort.
     * @param ui    User interface used to format the response.
     * @return Formatted sorting response.
     */
    @Override
    public String execute(TaskList tasks, Ui ui) {
        tasks.sort(field, order);
        return ui.showTasksSorted(field, order);
    }

    /**
     * Returns whether this command modifies the task list.
     *
     * @return True because the command sorts the task list.
     */
    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
