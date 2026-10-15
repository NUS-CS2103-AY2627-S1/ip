package pulbot.command;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/** Removes a task selected by its displayed number. */
public class DeleteCommand extends Command {
    private final String taskNumber;

    public DeleteCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Deletes the selected task and persists the updated list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException {
        int index = getTaskIndex(taskNumber, tasks.size());
        Task removedTask = tasks.remove(index);
        try {
            storage.save(tasks);
        } catch (PulbotException e) {
            tasks.insert(index, removedTask);
            throw e;
        }
        ui.showDeleted(removedTask, tasks.size());
    }
}
