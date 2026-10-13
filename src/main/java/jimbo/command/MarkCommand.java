package jimbo.command;

import jimbo.JimboException;
import jimbo.Storage;
import jimbo.TaskList;
import jimbo.ui.Ui;
import jimbo.task.Task;

/**
 * Command to mark a task as done.
 */
public class MarkCommand extends Command {
    private int index;

    /**
     *
     * @param index The index of the task to mark.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public String execute(Ui ui, TaskList tasks, Storage storage) throws JimboException {
        assert tasks != null : "tasks should not be null";

        if (index < 0 || index >= tasks.size()) {
            throw new JimboException("that's not a valid task :(");
        }
        Task task = tasks.get(index);
        if (task.isMarked()) {
            return "made the task even more done:\n " + task;
        }
        task.mark();
        return "good job for doing the task:\n" + task;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
