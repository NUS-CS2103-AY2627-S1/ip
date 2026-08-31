package bibi.command;

import bibi.BibiException;
import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Marks one task complete.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will complete the given task.
     *
     * @param taskNumber the one-based number shown to the user
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BibiException {
        tasks.get(taskNumber).markComplete();
        ui.showMessage("Marked task " + taskNumber + " as complete.");
        saveTasks(tasks, ui, storage);
    }
}
