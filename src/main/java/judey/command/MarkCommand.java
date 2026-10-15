package judey.command;

import judey.exception.JudeyException;
import judey.storage.Storage;
import judey.task.Task;
import judey.tasklist.TaskList;
import judey.ui.Ui;

/**
 * Represents a command that marks or unmarks a task as done
 */
public class MarkCommand extends Command {
    private final int index;
    private final boolean isDone;

    public MarkCommand(int index, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws JudeyException {
        Task task = isDone ? tasks.markAsDone(index) : tasks.markAsNotDone(index);
        storage.save(tasks.getTasks());
        ui.showTaskStatusChanged(task, isDone);
    }
}