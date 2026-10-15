package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Displays all tasks in the current task list. */
public class ListCommand extends Command {
    private final boolean isSortedByPriority;

    /** Creates a command that lists tasks in their underlying order. */
    public ListCommand() {
        this(false);
    }

    /**
     * Creates a command that lists tasks in the requested display order.
     *
     * @param isSortedByPriority whether to display a priority-sorted view.
     */
    public ListCommand(boolean isSortedByPriority) {
        this.isSortedByPriority = isSortedByPriority;
    }

    /**
     * Returns the current task list message created by the UI handler.
     *
     * @param tasks the current task list to display
     * @param ui the UI handler used to create the task-list message
     * @param storage the storage handler, which is not used by this command
     * @return the response listing the tasks
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.getTaskListMessage(tasks, isSortedByPriority);
    }
}
