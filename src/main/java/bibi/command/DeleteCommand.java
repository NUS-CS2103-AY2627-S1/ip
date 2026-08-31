package bibi.command;

import bibi.BibiException;
import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Removes one task from the list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will remove the given task.
     *
     * @param taskNumber the one-based number shown to the user
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the numbered task and saves the shortened list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BibiException {
        tasks.remove(taskNumber);
        ui.showMessage("Task " + taskNumber + " removed.");
        saveTasks(tasks, ui, storage);
    }
}
