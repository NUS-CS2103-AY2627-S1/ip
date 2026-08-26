package ultron.command;

import ultron.model.Task;
import ultron.model.TaskList;
import ultron.storage.Storage;
import ultron.ui.Ui;

/**
 * Removes a numbered task from the task list.
 */
public class DeleteCommand extends Command {
    private final String taskNumberText;

    /**
     * Creates a delete command with its task-number argument.
     *
     * @param taskNumberText the task number supplied by the user
     */
    public DeleteCommand(String taskNumberText) {
        this.taskNumberText = taskNumberText;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
            } else {
                Task deletedTask = tasks.remove(taskNumber - 1);
                ui.showTaskDeleted(deletedTask);
                storage.saveTasks(tasks.getTasks());
                ui.showTaskCount(tasks.size());
                ui.showSeparator();
            }
        } catch (NumberFormatException e) {
            ui.showInvalidTaskNumberFormat("delete");
        }
        ui.showSeparator();
    }
}
