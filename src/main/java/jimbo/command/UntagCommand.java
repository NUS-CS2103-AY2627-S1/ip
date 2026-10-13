package jimbo.command;

import jimbo.JimboException;
import jimbo.Storage;
import jimbo.TaskList;
import jimbo.task.Task;
import jimbo.ui.Ui;

/**
 * Command for removing a tag from a task.
 */
public class UntagCommand extends Command {
    private int index;
    private String tag;

    /**
     *
     * @param index The index of the task to remove a tag from.
     * @param tag The tag to remove.
     */
    public UntagCommand(int index, String tag) {
        this.index = index;
        this.tag = tag;
    }

    @Override
    public String execute(Ui ui, TaskList tasks, Storage storage) throws JimboException {
        if (index < 0 || index >= tasks.size()) {
            throw new JimboException("that's not a valid task :(");
        }
        Task task = tasks.get(index);
        task.removeTag(tag);
        return "who need tags anyway: \n" + task;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
