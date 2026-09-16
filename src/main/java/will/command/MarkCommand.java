package will.command;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.WillException;

/** The "mark &lt;task number&gt;" command: marks a task as done. */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that marks the task at the given index done.
     *
     * @param index Zero-based index of the task to mark done.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WillException {
        requireValidIndex(tasks, index);
        tasks.get(index).markAsDone();
        storage.save(tasks.getTasks());
        ui.showMessage("Amazing Gangie! I've marked this task as done:");
        ui.showMessage("  " + tasks.get(index).toString());
    }
}
