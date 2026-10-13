package jimbo.command;

import jimbo.JimboException;
import jimbo.Storage;
import jimbo.TaskList;
import jimbo.task.Task;
import jimbo.ui.Ui;

/**
 * Command for adding a tag to a task.
 */
public class TagCommand extends Command {
    private int index;
    private String tag;

    /**
     *
     * @param index The index of the task to add a tag to.
     * @param tag The tag to add.
     */
    public TagCommand(int index, String tag) {
        this.index = index;
        this.tag = tag;
    }

    @Override
    public String execute(Ui ui, TaskList tasks, Storage storage) throws JimboException {
        if (index < 0 || index >= tasks.size()) {
            throw new JimboException("that's not a valid task :(");
        }
        Task task = tasks.get(index);
        task.addTag(tag);
        return "i've put a little tag for this task:\n" + task;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
