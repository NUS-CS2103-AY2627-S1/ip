package jimbo.command;

import jimbo.JimboException;
import jimbo.Storage;
import jimbo.TaskList;
import jimbo.ui.Ui;
import jimbo.task.Task;

/**
 * Command for unmarking a task as done.
 */
public class UnmarkCommand extends Command {
    int index;

    /**
     *
     * @param index The index of the task to unmark.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public String execute(Ui ui, TaskList tasks, Storage storage) throws JimboException {
        assert tasks != null : "tasks should not be null";

        if (index < 0 || index >= tasks.size()) {
            throw new JimboException("that's not a valid task :(");
        }
        Task task = tasks.get(index);
        if (!task.isMarked()) {
            return "task was already not done... made the task even more undone:\n" + task;
        }
        task.unmark();
        return "oh... i guess you didn't finish the task:\n" + task;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
