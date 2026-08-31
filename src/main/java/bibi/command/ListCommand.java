package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskList;

/**
 * Shows every task, numbered as the user would refer to them.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that will show the whole task list.
     */
    public ListCommand() {
    }


    /**
     * Shows every task in order, or reports that the list is empty.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isEmpty()) {
            ui.showMessage("Your task list is empty.");
            return;
        }

        ui.showMessage("Here are the tasks in your list:");
        int taskNumber = 1;
        for (Task task : tasks.getTasks()) {
            ui.showNumberedTask(taskNumber, task);
            taskNumber++;
        }
    }
}
