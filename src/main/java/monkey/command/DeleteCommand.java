package monkey.command;

import java.util.List;

import monkey.model.Task;
import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for deleting a task by its one-based user index. */
public class DeleteCommand extends CommandAction {
    private final String taskNumber;

    public DeleteCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= tasks.size()) {
                ui.showMessage("That task number does not exist.");
                return;
            }
            List<Task> proposedTasks = tasks.asList();
            Task removedTask = proposedTasks.remove(index);
            if (!saveTasks(proposedTasks, ui, storage)) {
                return;
            }
            tasks.delete(index);
            ui.showMessage("Noted. I've removed this task:");
            ui.showMessage("  " + removedTask);
            ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
        } catch (NumberFormatException e) {
            ui.showMessage("OOPS! Monkey says: This monkey needs a valid task number after 'delete'.");
        }
    }
}
