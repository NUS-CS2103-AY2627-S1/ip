package charlie.command;

import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Marks one task as done.
 */
public class MarkCommand extends Command {
    /** Zero-based index of the task to mark. */
    private final int index;

    /**
     * Creates a command for the task at the given zero-based index.
     *
     * @param index Zero-based index of the task to mark.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the selected task, saves the task list, and displays the result.
     *
     * @param tasks Task collection containing the selected task.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        storage.save(tasks.getTasksAfterChangingStatus(index, true));
        Task markedTask = tasks.mark(index);
        ui.showMessages(
                "Tiny victory roar! This task is done:",
                "  " + markedTask);
    }
}
