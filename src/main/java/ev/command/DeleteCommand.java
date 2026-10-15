package ev.command;

import ev.EvException;
import ev.storage.Storage;
import ev.task.Task;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Removes one task from the list.
 */
public class DeleteCommand extends Command {

    private final int taskNumber;

    /**
     * Creates a command that will remove the given task.
     *
     * @param taskNumber the number the user gave, counting from 1.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EvException {
        Task removed = tasks.removeByNumber(taskNumber);
        ui.showRemoved(removed, tasks);
        storage.save(tasks.getTasks());
    }
}
